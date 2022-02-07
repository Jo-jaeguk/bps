package com.mobilityk.core.service

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.DamageCheckConfig
import com.mobilityk.core.domain.DamageCheckSearchOption
import com.mobilityk.core.domain.GuideDamageCheck
import com.mobilityk.core.domain.DamageLocation
import com.mobilityk.core.domain.DamageType
import com.mobilityk.core.dto.DamageCheckConfigDTO
import com.mobilityk.core.dto.GuideDamageCheckDTO
import com.mobilityk.core.dto.mapper.DamageCheckConfigMapper
import com.mobilityk.core.dto.mapper.GuideDamageCheckMapper
import com.mobilityk.core.dto.mapper.GuideMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.DamageCheckConfigRepository
import com.mobilityk.core.repository.GuideDamageCheckRepository
import com.mobilityk.core.repository.GuideRepository
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DamageCheckService(
    private val damageCheckRepository: GuideDamageCheckRepository,
    private val guideRepository: GuideRepository,
    private val damageCheckConfigRepository: DamageCheckConfigRepository,
    private val guideMapper: GuideMapper,
    private val damageCheckConfigMapper: DamageCheckConfigMapper,
    private val guideDamageCheckMapper: GuideDamageCheckMapper
) {
    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun findAllBySearch(search: DamageCheckSearchOption, pageable: Pageable): Page<DamageCheckConfigDTO> {
        return damageCheckConfigRepository.findAllBySearch(search, pageable)
    }

    @Transactional(readOnly = true)
    fun findByCarTypeAndDamageLocation(carType: CarTypeEnum, damageLocation: DamageLocation): DamageCheckConfigDTO {
        val damageChcekConfig = damageCheckConfigRepository.findByCarTypeAndDamageLocation(carType, damageLocation).orElseThrow { CommException("설정값이 없습니다.") }
        return damageCheckConfigMapper.toDto(damageChcekConfig)
    }




    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun initDamageCheckConfig(
        damageCheckConfigDTO: DamageCheckConfigDTO
    ): DamageCheckConfigDTO {
        val damageCheckConfigOpt = damageCheckConfigRepository.findByCarTypeAndDamageLocation(damageCheckConfigDTO.carType!!, damageCheckConfigDTO.damageLocation!!)
        return if(damageCheckConfigOpt.isPresent) {
            damageCheckConfigMapper.toDto(
                damageCheckConfigOpt.get()
            )
        } else {
            damageCheckConfigMapper.toDto(
                damageCheckConfigRepository.save(
                    DamageCheckConfig(
                        carType = damageCheckConfigDTO.carType,
                        damageLocation = damageCheckConfigDTO.damageLocation,
                        damageLocationStr = damageCheckConfigDTO.damageLocation?.description,
                        giDosaekPrice = damageCheckConfigDTO.giDosaekPrice,
                        giGyoHwanPrice = damageCheckConfigDTO.giGyoHwanPrice,
                        giPangumPrice = damageCheckConfigDTO.giPangumPrice,
                        needDosaekPrice = damageCheckConfigDTO.needDosaekPrice,
                        needGyoHwanPrice = damageCheckConfigDTO.needGyoHwanPrice,
                        needPangumPrice = damageCheckConfigDTO.needPangumPrice,
                        otherPrice = damageCheckConfigDTO.otherPrice
                    )
                )
            )
        }

    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun findAllByGuideId(guideId: Long): List<GuideDamageCheckDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guideDamageCheckMapper.toDtoList(damageCheckRepository.findAllByGuide(guide))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createDamage(
        guideId: Long,
        carType: CarTypeEnum,
        damageLocation: DamageLocation,
        damageType: DamageType
    ): GuideDamageCheckDTO {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }

        val damageConfig = damageCheckConfigRepository.findByCarTypeAndDamageLocation(carType, damageLocation).orElseThrow { CommException("가격 설정이 되어 있지 않습니다.") }
        var damagePrice = when(damageType) {
            DamageType.GI_GYOHWAN -> damageConfig.giGyoHwanPrice!!
            DamageType.GI_DOSAEK -> damageConfig.giDosaekPrice!!
            DamageType.GI_PANGUM -> damageConfig.giPangumPrice!!
            DamageType.NEED_DOSAEK -> damageConfig.needDosaekPrice!!
            DamageType.NEED_GYOHWAN -> damageConfig.needGyoHwanPrice!!
            DamageType.NEED_PANGUM -> damageConfig.needPangumPrice!!
            else -> 0
        }


        val damageCheck = GuideDamageCheck()
        damageCheck.create(
            guide = guide,
            carType = carType,
            damageLocation = damageLocation,
            damageType = damageType,
            price = damagePrice
        )
        return guideDamageCheckMapper.toDto(damageCheckRepository.save(damageCheck))
    }




}