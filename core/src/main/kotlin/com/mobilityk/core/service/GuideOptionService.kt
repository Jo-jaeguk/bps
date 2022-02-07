package com.mobilityk.core.service

import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuidePrice
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.domain.PriceConfigType
import com.mobilityk.core.dto.GuideOptionDTO
import com.mobilityk.core.dto.GuidePriceDTO
import com.mobilityk.core.dto.api.guide.GuidePriceCreateVM
import com.mobilityk.core.dto.api.guide.GuidePriceVM
import com.mobilityk.core.dto.mapper.GuideOptionMapper
import com.mobilityk.core.dto.mapper.GuidePriceMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarManufacturerRepository
import com.mobilityk.core.repository.GuideOptionRepository
import com.mobilityk.core.repository.GuidePriceConfigRepository
import com.mobilityk.core.repository.GuidePriceRepository
import com.mobilityk.core.repository.GuideRepository
import com.mobilityk.core.repository.MemberRepository
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.round

@Service
class GuideOptionService(
    private val activityLogService: ActivityLogService,
    private val guideRepository: GuideRepository,
    private val memberRepository: MemberRepository,
    private val guideOptionRepository: GuideOptionRepository,
    private val guideOptionMapper: GuideOptionMapper
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun findAllByGuide(guideId: Long): List<GuideOptionDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guideOptionMapper.toDtoList(guideOptionRepository.findAllByGuide(guide))
    }

}