import org.gradle.api.attributes.Bundling
import org.gradle.api.tasks.JavaExec

plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    kotlin("plugin.jpa") version "2.1.21"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.1.0"

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

repositories {
    mavenCentral()
}

val ktlint by configurations.creating {
    attributes {
        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    ktlint("com.pinterest.ktlint:ktlint-cli:1.5.0")
}

// Manual tasks: not dependencies of check, build, test or bootJar.
tasks.register<JavaExec>("ktlintCheck") {
    group = "verification"
    description = "Check Kotlin formatting when explicitly requested."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args("src/**/*.kt", "*.gradle.kts")
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Format Kotlin files when explicitly requested."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args("--format", "src/**/*.kt", "*.gradle.kts")
}

val frontendDist = layout.projectDirectory.dir("../frontend/dist")

tasks.processResources {
    from(frontendDist) {
        into("static")
    }
    // Remove old hashed assets when a frontend build changes or disappears.
    doFirst {
        delete(destinationDir)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    archiveFileName.set("app.jar")
    doFirst {
        check(frontendDist.file("index.html").asFile.isFile) {
            "Build the frontend first: cd ../frontend && npm ci && npm run build"
        }
    }
}
