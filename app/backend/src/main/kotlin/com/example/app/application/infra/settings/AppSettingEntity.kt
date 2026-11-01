package com.example.app.application.infra.settings

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "app_settings")
class AppSettingEntity(
    @Id
    @Column(name = "setting_key", length = 80)
    val key: String,
    @Column(name = "setting_value", nullable = false, length = 255)
    var value: String,
)
