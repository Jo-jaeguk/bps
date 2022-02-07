package com.mobilityk.core.service

import com.mobilityk.core.domain.CheckListConfig
import com.mobilityk.core.domain.ConfigType
import com.mobilityk.core.dto.CheckListConfigDTO
import com.mobilityk.core.dto.mapper.CheckListConfigMapper
import com.mobilityk.core.dto.mapper.ConfigMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CheckListConfigRepository
import com.mobilityk.core.repository.ConfigRepository
import com.mobilityk.core.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class CheckListConfigService(
    private val checkListRepository: CheckListConfigRepository,
    private val configRepository: ConfigRepository,
    private val memberRepository: MemberRepository,
    private val checkListMapper: CheckListConfigMapper,
    private val configMapper: ConfigMapper
) {

    @Transactional(readOnly = true)
    fun findAllByPublishedTrueOrderByOrderIndexAsc(): List<CheckListConfigDTO> {
        return checkListRepository.findAllByPublishedTrueOrderByOrderIndexAsc()
            .map { checkListMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<CheckListConfigDTO> {
        return checkListRepository.findAll(pageable)
            .map { checkListMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    fun findAllBySearch(search: String, pageable: Pageable): Page<CheckListConfigDTO> {
        return checkListRepository.findAllByQuestionContaining(search, pageable)
            .map { checkListMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    fun findAllBySearchExcel(): List<CheckListConfigDTO> {
        return checkListRepository.findAll()
            .map { checkListMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    fun getCheckListBasePrice(): Long {
        val configOpt = configRepository.findByConfigType(ConfigType.CHECK_LIST_BASE_PRICE)
        return if(configOpt.isEmpty) {
            0L
        } else {
            configOpt.get().value?.toLong()!!
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createCheckList(checkListDTO: CheckListConfigDTO, adminId: Long): CheckListConfigDTO {
        val member = memberRepository.findById(adminId).orElseThrow { CommException("not found member") }
        checkListDTO.writerLoginId = member.emailAddress

        val list = checkListRepository.findAll()
        var maxIndex = 0
        list.forEach { item ->
            if(item.orderIndex!! > maxIndex) {
                maxIndex = item.orderIndex!!
            }
        }
        if(maxIndex == 0) {
            maxIndex = 1
        } else {
            maxIndex++
        }
        checkListDTO.orderIndex = maxIndex

        val checkList = CheckListConfig()
        checkList.create(checkListDTO)
        return checkListMapper.toDto(checkListRepository.save(checkList))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteCheckList(id: Long) {
        checkListRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun modifyCheckList(id: Long, checkListDTO: CheckListConfigDTO): CheckListConfigDTO {
        val checkList = checkListRepository.findById(id).orElseThrow { CommException("not found data") }
        checkList.question = checkListDTO.question
        checkList.price = checkListDTO.price
        return checkListMapper.toDto(checkListRepository.save(checkList))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun publishModify(id: Long, checkListDTO: CheckListConfigDTO): CheckListConfigDTO {
        val checkList = checkListRepository.findById(id).orElseThrow { CommException("not found data") }
        checkList.published = checkListDTO.published
        return checkListMapper.toDto(checkListRepository.save(checkList))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun findById(id: Long): CheckListConfigDTO {
        val checkList = checkListRepository.findById(id).orElseThrow { CommException("not found data") }
        return checkListMapper.toDto(checkListRepository.save(checkList))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun orderUp(id: Long): CheckListConfigDTO {
        val checkList = checkListRepository.findById(id).orElseThrow { CommException("not found data") }

        val orderIndex = checkList.orderIndex!! - 1

        val list = checkListRepository.findAllByOrderIndex(orderIndex)
        if(!list.isNullOrEmpty()) {
            list.forEach { item ->
                item.orderIndex = checkList.orderIndex
                checkListRepository.save(item)
            }
            checkList.orderIndex = checkList.orderIndex!! - 1
        }

        return checkListMapper.toDto(checkListRepository.save(checkList))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun orderDown(id: Long): CheckListConfigDTO {
        val checkList = checkListRepository.findById(id).orElseThrow { CommException("not found data") }

        val orderIndex = checkList.orderIndex!! + 1

        val list = checkListRepository.findAllByOrderIndex(orderIndex)
        if(!list.isNullOrEmpty()) {
            list.forEach { item ->
                item.orderIndex = checkList.orderIndex
                checkListRepository.save(item)
            }
            checkList.orderIndex = checkList.orderIndex!! + 1
        }

        return checkListMapper.toDto(checkListRepository.save(checkList))
    }
}