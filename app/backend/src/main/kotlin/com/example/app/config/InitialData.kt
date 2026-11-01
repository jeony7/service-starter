package com.example.app.config

import com.example.app.application.infra.settings.AppSettingEntity
import com.example.app.application.infra.settings.AppSettingRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InitialData(private val settings: AppSettingRepository) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments) {
        if (!settings.existsById("application.name")) {
            settings.save(AppSettingEntity("application.name", "Service"))
        }
    }
}
