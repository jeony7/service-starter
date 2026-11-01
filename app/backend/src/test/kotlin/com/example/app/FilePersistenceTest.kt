package com.example.app

import com.example.app.application.infra.settings.AppSettingEntity
import com.example.app.application.infra.settings.AppSettingRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import java.nio.file.Path

class FilePersistenceTest {
    @TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun `file database preserves saved data across application restarts`() {
        val arguments = arrayOf(
            "--spring.datasource.url=jdbc:h2:file:${temporaryDirectory.resolve("app")}",
            "--spring.datasource.password=test-only",
            "--spring.jpa.hibernate.ddl-auto=update",
            "--spring.h2.console.enabled=false",
        )

        SpringApplicationBuilder(Application::class.java)
            .web(WebApplicationType.NONE).run(*arguments).use { first ->
                val repository = first.getBean(AppSettingRepository::class.java)
                repository.saveAndFlush(AppSettingEntity("persistence.probe", "saved"))
                repository.saveAndFlush(AppSettingEntity("application.name", "Persisted name"))
            }

        SpringApplicationBuilder(Application::class.java)
            .web(WebApplicationType.NONE).run(*arguments).use { second ->
                val repository = second.getBean(AppSettingRepository::class.java)
                assertThat(repository.findById("persistence.probe").orElseThrow().value).isEqualTo("saved")
                assertThat(repository.count()).isEqualTo(2)
                assertThat(repository.findById("application.name").orElseThrow().value).isEqualTo("Persisted name")
            }
    }
}
