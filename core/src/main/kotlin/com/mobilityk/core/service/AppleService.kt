package com.mobilityk.core.service

import com.mobilityk.core.repository.AppleAccountRepository
import com.mobilityk.core.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
data class AppleService(
    private val memberRepository: MemberRepository,
    private val appleAccountRepository: AppleAccountRepository
) {


}