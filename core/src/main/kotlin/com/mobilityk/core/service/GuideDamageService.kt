package com.mobilityk.core.service

import com.mobilityk.core.dto.GuideDamageCheckDTO
import com.mobilityk.core.dto.mapper.GuideDamageCheckMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.GuideDamageCheckRepository
import com.mobilityk.core.repository.GuideRepository
import com.mobilityk.core.repository.MemberRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GuideDamageService(
    private val activityLogService: ActivityLogService,
    private val guideRepository: GuideRepository,
    private val memberRepository: MemberRepository,
    private val guideDamageCheckRepository: GuideDamageCheckRepository,
    private val guideDamageCheckMapper: GuideDamageCheckMapper
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun findAllByGuide(guideId: Long): List<GuideDamageCheckDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guideDamageCheckMapper.toDtoList(guideDamageCheckRepository.findAllByGuide(guide))
    }

}