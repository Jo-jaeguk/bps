package com.mobilityk.core.service

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.Config
import com.mobilityk.core.domain.ConfigType
import com.mobilityk.core.domain.DamageCheckConfig
import com.mobilityk.core.domain.DamageLocation
import com.mobilityk.core.dto.ConfigDTO
import com.mobilityk.core.dto.api.config.DamageConfigRq
import com.mobilityk.core.dto.api.config.Price
import com.mobilityk.core.dto.mapper.CheckListConfigMapper
import com.mobilityk.core.dto.mapper.ConfigMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.ConfigRepository
import com.mobilityk.core.repository.DamageCheckConfigRepository
import com.mobilityk.core.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class ConfigService(
    private val configRepository: ConfigRepository,
    private val memberRepository: MemberRepository,
    private val damageCheckConfigRepository: DamageCheckConfigRepository,
    private val checkListMapper: CheckListConfigMapper,
    private val configMapper: ConfigMapper
) {

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ConfigDTO> {
        return configRepository.findAll(pageable)
            .map { configMapper.toDto(it) }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun findByConfigType(configType: ConfigType): ConfigDTO {
        val config = configRepository.findByConfigType(configType).orElseThrow { CommException("not found data") }
        return configMapper.toDto(configRepository.save(config))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getCheckListBasePrice(): String {
        val configOpt = configRepository.findByConfigType(ConfigType.CHECK_LIST_BASE_PRICE)
        return if(configOpt.isEmpty) {
            "0"
        } else {
            configOpt.get().value!!
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun upSert(configType: ConfigType, value: String): ConfigDTO {
        val configOpt = configRepository.findByConfigType(configType)
        return if(configOpt.isEmpty) {
            configMapper.toDto(
                configRepository.save(
                    Config(
                        configType = configType,
                        value = value
                    )
                )
            )
        } else {
            val config = configOpt.get()
            config.value = value
            configMapper.toDto(config)
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun saveDamageBasePrice(damageConfigRq: DamageConfigRq) {

        var targetValue = damageConfigRq.bigBasePrice.toString()
        var targetConfigType = ConfigType.DAMAGE_BASE_BIG_PRICE
        updateDamageRatio(targetConfigType, targetValue)

        targetValue = damageConfigRq.smallBasePrice.toString()
        targetConfigType = ConfigType.DAMAGE_BASE_SMALL_PRICE
        updateDamageRatio(targetConfigType, targetValue)

        targetValue = damageConfigRq.foreignBasePrice.toString()
        targetConfigType = ConfigType.DAMAGE_BASE_FOREIGN_PRICE
        updateDamageRatio(targetConfigType, targetValue)

        targetValue = damageConfigRq.rvBasePrice.toString()
        targetConfigType = ConfigType.DAMAGE_BASE_RV_PRICE
        updateDamageRatio(targetConfigType, targetValue)

        targetValue = damageConfigRq.midBasePrice.toString()
        targetConfigType = ConfigType.DAMAGE_BASE_MID_PRICE
        updateDamageRatio(targetConfigType, targetValue)

    }
    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun saveDamageBaseRate(damageConfigRq: DamageConfigRq) {

        var targetValue = damageConfigRq.bigBase.toString()
        var targetConfigType = ConfigType.DAMAGE_BASE_BIG_RATIO
        updateDamageRatio(targetConfigType, targetValue)

        targetValue = damageConfigRq.smallBase.toString()
        targetConfigType = ConfigType.DAMAGE_BASE_SMALL_RATIO
        updateDamageRatio(targetConfigType, targetValue)

        targetValue = damageConfigRq.foreignBase.toString()
        targetConfigType = ConfigType.DAMAGE_BASE_FOREIGN_RATIO
        updateDamageRatio(targetConfigType, targetValue)


        targetValue = damageConfigRq.rvBase.toString()
        targetConfigType = ConfigType.DAMAGE_BASE_RV_RATIO
        updateDamageRatio(targetConfigType, targetValue)

        targetValue = "1"
        targetConfigType = ConfigType.DAMAGE_BASE_MID_RATIO
        updateDamageRatio(targetConfigType, targetValue)

    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getDamageBaseRate(): DamageConfigRq {
        val damageConfigRq = DamageConfigRq()
        var configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_MID_RATIO)
        if(configOpt.isEmpty) {
            damageConfigRq.midBase = 0.0
        } else {
            damageConfigRq.midBase = configOpt.get().value?.toDouble()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_SMALL_RATIO)
        if(configOpt.isEmpty) {
            damageConfigRq.smallBase = 0.0
        } else {
            damageConfigRq.smallBase = configOpt.get().value?.toDouble()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_BIG_RATIO)
        if(configOpt.isEmpty) {
            damageConfigRq.bigBase = 0.0
        } else {
            damageConfigRq.bigBase = configOpt.get().value?.toDouble()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_FOREIGN_RATIO)
        if(configOpt.isEmpty) {
            damageConfigRq.foreignBase = 0.0
        } else {
            damageConfigRq.foreignBase = configOpt.get().value?.toDouble()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_RV_RATIO)
        if(configOpt.isEmpty) {
            damageConfigRq.rvBase = 0.0
        } else {
            val value = configOpt.get().value
            if(value.isNullOrEmpty() || value == "null") {
                damageConfigRq.rvBase = 0.0
            } else {
                damageConfigRq.rvBase = configOpt.get().value?.toDouble()
            }
        }
        return damageConfigRq

    }



    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getDamageBasePrice(): DamageConfigRq {
        val damageConfigRq = DamageConfigRq()
        var configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_MID_PRICE)
        if(configOpt.isEmpty) {
            damageConfigRq.midBasePrice = 0
        } else {
            damageConfigRq.midBasePrice = configOpt.get().value?.toLong()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_SMALL_PRICE)
        if(configOpt.isEmpty) {
            damageConfigRq.smallBasePrice = 0
        } else {
            damageConfigRq.smallBasePrice = configOpt.get().value?.toLong()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_BIG_PRICE)
        if(configOpt.isEmpty) {
            damageConfigRq.bigBasePrice = 0
        } else {
            damageConfigRq.bigBasePrice = configOpt.get().value?.toLong()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_FOREIGN_PRICE)
        if(configOpt.isEmpty) {
            damageConfigRq.foreignBasePrice = 0
        } else {
            damageConfigRq.foreignBasePrice = configOpt.get().value?.toLong()
        }

        configOpt = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_RV_PRICE)
        if(configOpt.isEmpty) {
            damageConfigRq.rvBasePrice = 0
        } else {
            val value = configOpt.get().value
            if(value.isNullOrEmpty() || value == "null") {
                damageConfigRq.rvBasePrice = 0
            } else {
                damageConfigRq.rvBasePrice = configOpt.get().value?.toLong()
            }
        }
        return damageConfigRq

    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun saveDamagePrice(damageConfigRq: DamageConfigRq) {

        if(damageConfigRq.carType != CarTypeEnum.MID) {
            throw CommException("중형차 기준 설정만 가능합니다.")
        }

        val bigRatioConfig = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_BIG_RATIO).orElseThrow { CommException("비율 설정이 되어 있지 않습니다.") }
        val smallRatioConfig = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_SMALL_RATIO).orElseThrow { CommException("비율 설정이 되어 있지 않습니다.") }
        val foreignRatioConfig = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_FOREIGN_RATIO).orElseThrow { CommException("비율 설정이 되어 있지 않습니다.") }
        val rvRatioConfig = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_RV_RATIO).orElseThrow { CommException("비율 설정이 되어 있지 않습니다.") }

        val bigRt = bigRatioConfig.value?.toDouble()!!
        val smallRt = smallRatioConfig.value?.toDouble()!!
        val foreignRt = foreignRatioConfig.value?.toDouble()!!
        val rvRt = rvRatioConfig.value?.toDouble()!!


        //val carType = damageConfigRq.carType

        damageConfigRq.priceList?.forEach { price ->
            CarTypeEnum.values().forEach { carTypeEnum ->
                when(carTypeEnum) {
                    CarTypeEnum.SMALL -> {
                        saveDamagePriceConfig(
                            carTypeEnum,
                            price.damageLocation!!,
                            giGyoHwanPrice = (price.giGyoHwanPrice!! * smallRt).toLong(),
                            giPangumPrice = (price.giPangumPrice!! * smallRt).toLong(),
                            giDosaekPrice = (price.giDosaekPrice!! * smallRt).toLong(),
                            needPangumPrice = (price.needPangumPrice!! * smallRt).toLong(),
                            needGyoHwanPrice = (price.needGyoHwanPrice!! * smallRt).toLong(),
                            needDosaekPrice = (price.needDosaekPrice!! * smallRt).toLong(),
                            otherPrice = (price.otherPrice!! * smallRt).toLong(),
                        )
                    }
                    CarTypeEnum.BIG -> {
                        saveDamagePriceConfig(
                            carTypeEnum,
                            price.damageLocation!!,
                            giGyoHwanPrice = (price.giGyoHwanPrice!! * bigRt).toLong(),
                            giPangumPrice = (price.giPangumPrice!! * bigRt).toLong(),
                            giDosaekPrice = (price.giDosaekPrice!! * bigRt).toLong(),
                            needPangumPrice = (price.needPangumPrice!! * bigRt).toLong(),
                            needGyoHwanPrice = (price.needGyoHwanPrice!! * bigRt).toLong(),
                            needDosaekPrice = (price.needDosaekPrice!! * bigRt).toLong(),
                            otherPrice = (price.otherPrice!! * bigRt).toLong(),
                        )
                    }
                    CarTypeEnum.FOREIGN -> {
                        saveDamagePriceConfig(
                            carTypeEnum,
                            price.damageLocation!!,
                            giGyoHwanPrice = (price.giGyoHwanPrice!! * foreignRt).toLong(),
                            giPangumPrice = (price.giPangumPrice!! * foreignRt).toLong(),
                            giDosaekPrice = (price.giDosaekPrice!! * foreignRt).toLong(),
                            needPangumPrice = (price.needPangumPrice!! * foreignRt).toLong(),
                            needGyoHwanPrice = (price.needGyoHwanPrice!! * foreignRt).toLong(),
                            needDosaekPrice = (price.needDosaekPrice!! * foreignRt).toLong(),
                            otherPrice = (price.otherPrice!! * foreignRt).toLong(),
                        )
                    }
                    CarTypeEnum.RV -> {
                        saveDamagePriceConfig(
                            carTypeEnum,
                            price.damageLocation!!,
                            giGyoHwanPrice = (price.giGyoHwanPrice!! * rvRt).toLong(),
                            giPangumPrice = (price.giPangumPrice!! * rvRt).toLong(),
                            giDosaekPrice = (price.giDosaekPrice!! * rvRt).toLong(),
                            needPangumPrice = (price.needPangumPrice!! * rvRt).toLong(),
                            needGyoHwanPrice = (price.needGyoHwanPrice!! * rvRt).toLong(),
                            needDosaekPrice = (price.needDosaekPrice!! * rvRt).toLong(),
                            otherPrice = (price.otherPrice!! * rvRt).toLong(),
                        )
                    }
                    CarTypeEnum.MID -> {
                        saveDamagePriceConfig(
                            carTypeEnum,
                            price.damageLocation!!,
                            giGyoHwanPrice = price.giGyoHwanPrice!!,
                            giPangumPrice = price.giPangumPrice!!,
                            giDosaekPrice = price.giDosaekPrice!!,
                            needPangumPrice = price.needPangumPrice!!,
                            needGyoHwanPrice = price.needGyoHwanPrice!!,
                            needDosaekPrice = price.needDosaekPrice!!,
                            otherPrice = price.otherPrice!!,
                        )
                    }
                }
            }

        }

    }

    fun saveDamagePriceConfig(carType: CarTypeEnum, damageLocation: DamageLocation,
        giGyoHwanPrice: Long,
        giDosaekPrice: Long,
        giPangumPrice: Long,
        needDosaekPrice: Long,
        needGyoHwanPrice: Long,
        needPangumPrice: Long,
        otherPrice: Long,
    ) {
        val damageCheckOpt = damageCheckConfigRepository.findByCarTypeAndDamageLocation(carType, damageLocation)
        if(damageCheckOpt.isEmpty) {
            damageCheckConfigRepository.save(
                DamageCheckConfig(
                    carType = carType,
                    damageLocation = damageLocation,
                    damageLocationStr = damageLocation.description,
                    giGyoHwanPrice = giGyoHwanPrice,
                    giDosaekPrice = giDosaekPrice,
                    giPangumPrice = giPangumPrice,
                    needDosaekPrice = needDosaekPrice,
                    needGyoHwanPrice = needGyoHwanPrice,
                    needPangumPrice = needPangumPrice,
                    otherPrice = otherPrice
                )
            )
        } else {
            val damageCheck = damageCheckOpt.get()
            damageCheck.giGyoHwanPrice = giGyoHwanPrice
            damageCheck.giDosaekPrice = giDosaekPrice
            damageCheck.giPangumPrice = giPangumPrice
            damageCheck.needDosaekPrice = needDosaekPrice
            damageCheck.needGyoHwanPrice = needGyoHwanPrice
            damageCheck.needPangumPrice = needPangumPrice
            damageCheck.otherPrice = otherPrice
            damageCheckConfigRepository.save(damageCheck)
        }
    }

    fun updateDamageRatio(configType: ConfigType, value: String) {
        val configOpt = configRepository.findByConfigType(configType)
        if(configOpt.isEmpty) {
            configRepository.save(
                Config(
                    configType = configType,
                    value = value
                )
            )
        } else {
            val config = configOpt.get()
            config.value = value
            configRepository.save(config)
        }
    }

    fun updateDamagePrice(configType: ConfigType, value: String) {
        val configOpt = configRepository.findByConfigType(configType)
        if(configOpt.isEmpty) {
            configRepository.save(
                Config(
                    configType = configType,
                    value = value
                )
            )
        } else {
            val config = configOpt.get()
            config.value = value
            configRepository.save(config)
        }
    }
}