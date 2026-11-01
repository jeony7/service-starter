package com.example.app

import com.example.app.application.infra.settings.AppSettingEntity
import com.example.app.application.infra.settings.AppSettingRepository
import com.example.app.config.InitialData
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest(properties = [
    "spring.datasource.url=jdbc:h2:mem:starter-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.password=test-only",
    "spring.jpa.hibernate.ddl-auto=create-drop",
])
@AutoConfigureMockMvc
class StarterIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val settings: AppSettingRepository,
    private val initialData: InitialData,
    private val transactions: TransactionTemplate,
) {
    @Test
    fun `health reads seeded application data through JPA`() {
        mockMvc.get("/api/health").andExpect {
            status { isOk() }
            jsonPath("$.status") { value("UP") }
            jsonPath("$.database") { value("UP") }
            jsonPath("$.applicationName") { value("Service") }
        }
    }

    @Test
    fun `initial data can run again without duplicate rows or overwriting changes`() {
        settings.saveAndFlush(AppSettingEntity("application.name", "Custom name"))
        try {
            initialData.run(DefaultApplicationArguments())
            assertThat(settings.count()).isEqualTo(1)
            assertThat(settings.findById("application.name").orElseThrow().value).isEqualTo("Custom name")
        } finally {
            settings.saveAndFlush(AppSettingEntity("application.name", "Service"))
        }
    }

    @Test
    fun `failed transaction leaves no partially saved setting`() {
        assertThatThrownBy {
            transactions.executeWithoutResult {
                settings.saveAndFlush(AppSettingEntity("rollback.probe", "temporary"))
                throw IllegalStateException("rollback probe")
            }
        }.isInstanceOf(IllegalStateException::class.java)
        assertThat(settings.existsById("rollback.probe")).isFalse()
    }
}
