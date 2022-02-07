package com.mobilityk.core.repository

import com.mobilityk.core.domain.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository: JpaRepository<Notification, Long> {
    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<Notification>
}