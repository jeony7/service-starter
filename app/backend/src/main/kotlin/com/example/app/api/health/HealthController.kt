package com.example.app.api.health

import com.example.app.application.service.health.HealthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController(private val healthService: HealthService) {
    @GetMapping("/api/health")
    fun health(): HealthResponse = HealthResponse(
        status = "UP",
        database = "UP",
        applicationName = healthService.readApplicationName(),
    )
}
