package com.mobilityk.core.repository

import com.mobilityk.core.domain.KakaoTalkPushHistory
import org.springframework.data.jpa.repository.JpaRepository

interface KakaoTalkPushHistoryRepository: JpaRepository<KakaoTalkPushHistory, Long> {
}