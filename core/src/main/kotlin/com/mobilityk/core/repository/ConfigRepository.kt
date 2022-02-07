package com.mobilityk.core.repository

import com.mobilityk.core.domain.Config
import com.mobilityk.core.domain.ConfigType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ConfigRepository: JpaRepository<Config, Long> {
    fun findByConfigType(configType: ConfigType): Optional<Config>
}