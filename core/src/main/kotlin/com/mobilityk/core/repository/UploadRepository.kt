package com.mobilityk.core.repository

import com.mobilityk.core.domain.Upload
import org.springframework.data.jpa.repository.JpaRepository

interface UploadRepository: JpaRepository<Upload, Long> {
}