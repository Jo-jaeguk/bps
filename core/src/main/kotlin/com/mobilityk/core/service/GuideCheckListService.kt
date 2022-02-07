package com.mobilityk.core.service

import com.mobilityk.core.domain.CheckListConfig
import com.mobilityk.core.domain.ConfigType
import com.mobilityk.core.domain.GuideCheckList
import com.mobilityk.core.dto.CheckListConfigDTO
import com.mobilityk.core.dto.GuideCheckListDTO
import com.mobilityk.core.dto.GuideCheckListMapper
import com.mobilityk.core.dto.mapper.CheckListConfigMapper
import com.mobilityk.core.dto.mapper.ConfigMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CheckListConfigRepository
import com.mobilityk.core.repository.ConfigRepository
import com.mobilityk.core.repository.GuideCheckListRepository
import com.mobilityk.core.repository.GuideRepository
import com.mobilityk.core.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class GuideCheckListService(
    private val checkListConfigRepository: CheckListConfigRepository,
    private val guideCheckListRepository: GuideCheckListRepository,
    private val guideRepository: GuideRepository,
    private val guideCheckListMapper: GuideCheckListMapper,
) {

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createCheckList(guideId: Long, checkListDTO: GuideCheckListDTO): GuideCheckListDTO {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }

        val checkList = checkListConfigRepository.findById(checkListDTO.checkListConfigId!!).orElseThrow { CommException("not found checkList") }

        val guideCheckList = GuideCheckList()
        guideCheckList.create(
            guide = guide,
            question = checkList.question!!,
            price = checkList.price!!
        )
        return guideCheckListMapper.toDto(guideCheckListRepository.save(guideCheckList))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun findAllByGuideId(guideId: Long): List<GuideCheckListDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guideCheckListMapper.toDtoList(guideCheckListRepository.findAllByGuide(guide))
    }

}