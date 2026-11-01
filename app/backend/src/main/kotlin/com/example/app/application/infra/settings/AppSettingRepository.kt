package com.example.app.application.infra.settings

import org.springframework.data.jpa.repository.JpaRepository

interface AppSettingRepository : JpaRepository<AppSettingEntity, String>
