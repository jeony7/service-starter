package com.example.app.application.service.health

import com.example.app.application.infra.settings.AppSettingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HealthService(private val settings: AppSettingRepository) {
    @Transactional(readOnly = true)
    fun readApplicationName(): String = settings.findById("application.name")
        .orElseThrow { IllegalStateException("Application settings are not initialized") }
        .value
}
