package com.mobilityk.core.service

import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.GuidePriceConfig
import com.mobilityk.core.domain.GuidePriceConfigComment
import com.mobilityk.core.domain.PriceConfigType
import com.mobilityk.core.dto.GuidePriceConfigCommentDTO
import com.mobilityk.core.dto.GuidePriceConfigDTO
import com.mobilityk.core.dto.api.guidePriceConfig.EverageVM
import com.mobilityk.core.dto.api.guidePriceConfig.TableVM
import com.mobilityk.core.dto.mapper.GuidePriceConfigCommentMapper
import com.mobilityk.core.dto.mapper.GuidePriceConfigMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.GuidePriceConfigCommentRepository
import com.mobilityk.core.repository.GuidePriceConfigRepository
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class GuidePriceConfigService(
    private val guidePriceConfigRepository: GuidePriceConfigRepository,
    private val guidePriceConfigCommentRepository: GuidePriceConfigCommentRepository,
    private val activityLogService: ActivityLogService,
    private val guidePriceConfigMapper: GuidePriceConfigMapper,
    private val guidePriceConfigCommentMapper: GuidePriceConfigCommentMapper
) {
    private val logger = KotlinLogging.logger {}
    @Transactional
    fun findAll(): List<GuidePriceConfigDTO> {
        return guidePriceConfigMapper.toDtoList(guidePriceConfigRepository.findAll())
    }

    @Transactional
    fun findAllBy(priceConfigTye: PriceConfigType?): List<GuidePriceConfigDTO> {
        return if(priceConfigTye == null) {
            guidePriceConfigMapper.toDtoList(guidePriceConfigRepository.findAll())
        } else {
            guidePriceConfigMapper.toDtoList(guidePriceConfigRepository.findAllByPriceConfigType(priceConfigTye))
        }
    }

    @Transactional
    fun updateRetailConfig(adminId: Long, retailValue: Double): Unit {
        val config = guidePriceConfigRepository.findByPriceConfigType(PriceConfigType.RETAIL_CONST)
        var beforeValue = "0"
        if(config == null) {
            guidePriceConfigRepository.save(
                GuidePriceConfig(
                    value = retailValue.toString(),
                    priceConfigType = PriceConfigType.RETAIL_CONST
                )
            )
        } else {
            beforeValue = config.value!!
            config.value = retailValue.toString()
            guidePriceConfigRepository.save(config)
        }
        activityLogService.createGuideConfigRetailLog(adminId, beforeValue = beforeValue.toDouble(), afterValue = retailValue)
    }

    @Transactional
    fun updateEverage(adminId: Long, everageVM: EverageVM): Unit {
        val builder = StringBuilder()
        val domesticConfig = guidePriceConfigRepository.findByCountryTypeAndPriceConfigType(CountryType.DOMESTIC, PriceConfigType.AVERAGE)
        val internationalConfig = guidePriceConfigRepository.findByCountryTypeAndPriceConfigType(CountryType.INTERNATIONAL, PriceConfigType.AVERAGE)
        var beforeValue = "0"

        if(domesticConfig == null) {
            guidePriceConfigRepository.save(
                GuidePriceConfig(
                    value = (everageVM.domesticEverageValue?.toInt()).toString(),
                    priceConfigType = PriceConfigType.AVERAGE,
                    countryType = CountryType.DOMESTIC
                )
            )
        } else {
            beforeValue = domesticConfig.value!!
            domesticConfig.value = (everageVM.domesticEverageValue?.toInt()).toString()
            guidePriceConfigRepository.save(domesticConfig)
        }
        builder.append("차량 평균가 설정 국내차 (").append(beforeValue).append(") -> (").append(everageVM.domesticEverageValue).append(")").append("\n")

        beforeValue = "0"
        if(internationalConfig == null) {
            guidePriceConfigRepository.save(
                GuidePriceConfig(
                    value = (everageVM.internationalEverageValue?.toInt()).toString(),
                    priceConfigType = PriceConfigType.AVERAGE,
                    countryType = CountryType.INTERNATIONAL
                )
            )
        } else {
            beforeValue = internationalConfig.value!!
            internationalConfig.value = (everageVM.internationalEverageValue?.toInt()!!).toString()
            guidePriceConfigRepository.save(internationalConfig)
        }
        builder.append("차량 평균가 설정 수입차 (").append(beforeValue).append(") -> (").append(everageVM.internationalEverageValue).append(")").append("\n")
        activityLogService.createGuideConfigEverageLog(adminId, builder.toString())
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updatePriceTable(adminId: Long, tableVM: TableVM): Unit {
        //val priceConfigList = guidePriceConfigRepository.findAll()

        val builder = StringBuilder()
        tableVM.tablePriceList?.forEach { afterPrice ->
            var beforePriceValue = 0.0
            val realPrice = afterPrice.value ?: 0.0
            val priceConfig = guidePriceConfigRepository.findByCountryTypeAndPriceConfigTypeAndPopularType(
                afterPrice.countryType!!,
                afterPrice.priceConfigType!!,
                afterPrice.popularType!!
            )
            if(priceConfig == null) {
                guidePriceConfigRepository.save(
                    GuidePriceConfig(
                        countryType = afterPrice.countryType,
                        popularType = afterPrice.popularType,
                        priceConfigType = afterPrice.priceConfigType,
                        value = realPrice.toString()
                    )
                )
            } else {
                beforePriceValue = priceConfig.value!!.toDouble()
                priceConfig.updateTableData(afterPrice, realPrice = realPrice.toString())
            }

            var afterPriceValue = afterPrice.value ?: 0.0
            var configType = afterPrice.priceConfigType?.description
            var popularType = afterPrice.popularType?.description
            var countrtType = afterPrice.countryType?.description
            /*
            saveList.forEach { beforePrice ->
                if(afterPrice.priceConfigType == beforePrice.priceConfigType &&
                    afterPrice.popularType == beforePrice.popularType &&
                    afterPrice.countryType == beforePrice.countryType) {
                    beforePriceValue = beforePrice.value?.toInt()!!
                }
            }
             */
            logger.info { "countryType ${afterPrice.countryType!!} priceConfigType ${afterPrice.priceConfigType!!} popularType ${afterPrice.popularType!!}" }
            if(beforePriceValue != afterPriceValue) {
                logger.info { "before $beforePriceValue after $afterPriceValue" }
                builder.append("Table ").append(configType).append(" ")
                    .append(countrtType).append(" ")
                    .append(popularType).append(" ")
                    .append(CommonUtil.comma(beforePriceValue.toInt())).append("만원").append(" -> ")
                    .append(CommonUtil.comma(afterPriceValue.toInt())).append("만원")
                    .append("변경").append("\n")
            }
        }

        if(!builder.isNullOrEmpty()) {
            activityLogService.createGuideConfigTableLog(adminId, builder.toString())
        }
    }

    @Transactional
    fun createComment(adminId: Long, guidePriceConfigCommentDTO: GuidePriceConfigCommentDTO): GuidePriceConfigCommentDTO {
        return guidePriceConfigCommentMapper.toDto(
            guidePriceConfigCommentRepository.save(
                GuidePriceConfigComment(
                    body = guidePriceConfigCommentDTO.body,
                    writerId = adminId
                )
            )
        )
    }

    @Transactional
    fun deleteComment(adminId: Long, commentId: Long) {
        val comment = guidePriceConfigCommentRepository.findById(commentId).orElseThrow { CommException("not found comment") }
        if(comment.writerId != adminId) {
            throw CommException("no permission")
        }
        guidePriceConfigCommentRepository.delete(comment)
    }
}