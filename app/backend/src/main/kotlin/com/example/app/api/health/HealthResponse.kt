package com.example.app.api.health

data class HealthResponse(
    val status: String,
    val database: String,
    val applicationName: String,
)
