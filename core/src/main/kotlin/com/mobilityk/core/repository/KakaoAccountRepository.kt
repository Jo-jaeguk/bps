package com.mobilityk.core.repository

import com.mobilityk.core.domain.KakaoAccount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface KakaoAccountRepository: JpaRepository<KakaoAccount, Long> {
    fun findByKakaoId(kakaoId: Long): Optional<KakaoAccount>
}