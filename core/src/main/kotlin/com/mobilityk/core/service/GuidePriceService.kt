package com.mobilityk.core.service

import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuidePrice
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.domain.PriceConfigType
import com.mobilityk.core.dto.GuidePriceDTO
import com.mobilityk.core.dto.api.guide.GuidePriceCreateVM
import com.mobilityk.core.dto.api.guide.GuidePriceVM
import com.mobilityk.core.dto.mapper.GuidePriceMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarManufacturerRepository
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
class GuidePriceService(
    private val activityLogService: ActivityLogService,
    private val guideRepository: GuideRepository,
    private val guidePriceRepository: GuidePriceRepository,
    private val guidePriceConfigRepository: GuidePriceConfigRepository,
    private val memberRepository: MemberRepository,
    private val carClassRepository: CarClassRepository,
    private val guidePriceMapper: GuidePriceMapper,
    private val carManufacturerRepository: CarManufacturerRepository
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun findAll(pageable: Pageable): Page<GuidePriceDTO> {
        return guidePriceRepository.findAll(pageable)
            .map { guidePrice ->
                guidePriceMapper.toDto(guidePrice)
            }
    }

    @Transactional
    fun findAllByGuideId(guideId: Long): List<GuidePriceDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guidePriceRepository.findAllByGuide(guide)
            .map { guidePriceMapper.toDto(it) }
    }

    @Transactional
    fun findByGuidePriceId(guidePriceID: Long): GuidePriceDTO {
        val guidePrice = guidePriceRepository.findById(guidePriceID).orElseThrow { CommException("not found guide price") }
        return guidePriceMapper.toDto(guidePrice)
    }



    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun upsertGuidePrice(guideId: Long, adminId: Long, guidePriceVM: GuidePriceCreateVM): Long {
        val guide = guideRepository.findById(guideId)
        if(guide.isEmpty) throw CommException("not found guide")

        if(guidePriceVM.priceList.isNullOrEmpty()) throw CommException("invalid price")

        val storedGuide = guide.get()
        //val beforePriceList = storedGuide.priceList

        val member = memberRepository.findById(storedGuide.memberId!!).orElseThrow { CommException("nout found user") }

        val builder = StringBuilder()
        builder.append(member.name + "고객 가이드 수정").append("\n")
        builder.append("가이드 번호 [" + storedGuide.serial + "]").append("\n")

        val deleteGuidePriceList = mutableListOf<GuidePrice>()

        var modifyCnt = 0
        var retailGuideCount = 0
        guidePriceVM.priceList?.let {
            it.forEach { guidePriceVM ->
                val guidePriceList = guidePriceRepository.findByGuideAndMarketNameAndMarketType(
                    storedGuide,
                    guidePriceVM.marketName!!,
                    guidePriceVM.marketType!!
                )
                var priceIndex = 0

                guidePriceList.forEach { guidePrice ->
                    if(priceIndex > 0) {
                        deleteGuidePriceList.add(guidePrice)
                    }
                }

                if(guidePriceVM.marketType == MarketType.RETAIL && guidePriceVM.price!! > 0) {
                    retailGuideCount++
                }

                if(!guidePriceList.isNullOrEmpty()) {
                    val guidePrice = guidePriceList[0]
                    if(guidePrice.price != guidePriceVM.price){
                        modifyCnt++

                        builder.append(guidePrice.marketType?.description).append(" ")
                        builder.append(guidePrice.marketName).append(" ")
                        builder.append("[").append(guidePrice.price).append("]만원")
                        builder.append(" -> ")
                        builder.append("[").append(guidePriceVM.price).append("]만원 변경")
                            .append("\n")
                    }
                    guidePrice.price = guidePriceVM.price!!
                } else {
                    modifyCnt++
                    builder.append(guidePriceVM.marketType?.description).append(" ")
                    builder.append(guidePriceVM.marketName).append(" ")
                    builder.append("[0]만원")
                    builder.append(" -> ")
                    builder.append("[").append(guidePriceVM.price).append("]만원 변경")
                        .append("\n")
                    guidePriceRepository.save(
                        GuidePrice(
                            price = guidePriceVM.price!!,
                            marketName = guidePriceVM.marketName,
                            marketType = guidePriceVM.marketType,
                            guide = storedGuide
                        )
                    )
                }
            }
        }

        if(!deleteGuidePriceList.isNullOrEmpty()) {
            deleteGuidePriceList.forEach { deleteGuidePrice ->
                guidePriceRepository.delete(deleteGuidePrice)
            }
        }

        logger.info { "소매가 가이드 가격 개수 $retailGuideCount" }
        var guidePrice = 0L
        if(retailGuideCount >= 3) {
            guidePrice = getGuidePrice(guidePriceVM, storedGuide)
            storedGuide.price = guidePrice
        } else {
            storedGuide.price = 0
        }

        if(modifyCnt > 0) {
            activityLogService.createGuideLogV2(member, adminId, builder.toString())
        }
        return guidePrice

    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun upsertGuidePriceOne(guideId: Long, adminId: Long, guidePriceVM: GuidePriceVM): GuidePriceDTO {
        val guideOpt = guideRepository.findById(guideId)
        if(guideOpt.isEmpty) throw CommException("not found guide")

        val guide = guideOpt.get()
        //val beforePriceList = storedGuide.priceList

        val member = memberRepository.findById(guide.memberId!!).orElseThrow { CommException("nout found user") }

        val builder = StringBuilder()
        builder.append(member.name + "고객 가이드 수정").append("\n")
        builder.append("가이드 번호 [" + guide.serial + "]").append("\n")

        val guidePriceOpt = guidePriceRepository.findByGuideAndMarketNameCodeAndMarketType(
            guide,
            guidePriceVM.marketNameCode!!,
            guidePriceVM.marketType!!
        )

        var guidePrice: GuidePrice
        if(guidePriceOpt.isPresent) {
            guidePrice = guidePriceOpt.get()
            if(guidePrice.price != guidePriceVM.price){
                builder.append(guidePrice.marketType?.description).append(" ")
                builder.append(guidePrice.marketName).append(" ")
                builder.append("[").append(guidePrice.price).append("]만원")
                builder.append(" -> ")
                builder.append("[").append(guidePriceVM.price).append("]만원 변경")
                    .append("\n")
            }
            guidePrice.price = guidePriceVM.price!!
            guidePrice.popularType = guidePriceVM.popularType
        } else {
            builder.append(guidePriceVM.marketType?.description).append(" ")
            builder.append(guidePriceVM.marketName).append(" ")
            builder.append("[0]만원")
            builder.append(" -> ")
            builder.append("[").append(guidePriceVM.price).append("]만원 변경")
                .append("\n")
            guidePrice = guidePriceRepository.save(
                GuidePrice(
                    price = guidePriceVM.price!!,
                    marketName = guidePriceVM.marketName,
                    marketType = guidePriceVM.marketType,
                    guide = guide,
                    popularType = guidePriceVM.popularType,
                    marketNameCode = guidePriceVM.marketNameCode
                )
            )
        }

        val guidePriceDTO = guidePriceMapper.toDto(guidePrice)



        val guidePrices = guidePriceRepository.findAllByGuideAndMarketType(guide, guidePriceVM.marketType!!)

        var count = guidePrices.filter { it.price != null && it.price!! > 0 }.size
        var totalPrice = 0L
        var popularCnt = 0
        guidePrices.forEach { innerGuidePrice ->
            totalPrice += innerGuidePrice.price!!
            // 이건 꼭 은 인기와 동일하게 취급
            if(innerGuidePrice.popularType == PopularType.POPULAR || innerGuidePrice.popularType == PopularType.ESSENTIAL) {
                popularCnt++
            }
        }

        // 소매가평균 - 차량 테이블 데이터 (인기 , 국산) * (소매가평균 / 테이블 구간 평균)
        if(guidePriceVM.marketType == MarketType.RETAIL) {

            if(count == 0) {
                guide.retailAvgPrice = 0L
                guidePriceDTO.retailAvgPrice = 0L
            } else {
                val avg = totalPrice / count
                guide.retailAvgPrice = avg
                guidePriceDTO.retailAvgPrice = avg

                // 가이드두개만 선택되어도 가이드 가격 나오게 변경
                if(count >= 2) {
                    var popularType = if(popularCnt >= 2) PopularType.POPULAR else PopularType.UN_POPULAR
                    val guidePriceValue = getGuidePrice(avg, guide, popularType)
                    guide.price = guidePriceValue
                    guidePriceDTO.guidePrice = guidePriceValue
                }
            }

        } else {
            if(count == 0) {
                guide.bidAvgPrice = 0L
                guidePriceDTO.bidAvgPrice = 0L
            } else {
                val avg = totalPrice / count
                guide.bidAvgPrice = avg
                guidePriceDTO.bidAvgPrice = avg
            }
        }

        guideRepository.save(guide)
        activityLogService.createGuideLogV2(member, adminId, builder.toString())

        guidePriceDTO.guidePrice = guide.price
        guidePriceDTO.sendPrice = if(guide.sendPrice == null || guide.sendPrice == 0L) {
            guide.price
        } else {
            guide.sendPrice
        }

        return guidePriceDTO
    }

    fun getGuidePrice(retailAvg: Long, guide: Guide, popularType: PopularType): Long {

        // 소매가평균 - 차량 테이블 데이터 (인기 , 국산) * (소매가평균 / 테이블 구간 평균)

        carClassRepository.findById(guide.carClassId!!).orElseThrow { CommException("not found carClass") }
        val carManufacturer = carManufacturerRepository.findById(guide.carManufacturerId!!).orElseThrow { CommException("not found carManufacturer") }

        var tableAvg: Double
        var tableValue: Double
        when(retailAvg) {
            in 0..100 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_100, popularType)
            }
            in 101 .. 200 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_200, popularType)
            }
            in 201 .. 300 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_300, popularType)
            }
            in 301 .. 400 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_400, popularType)
            }
            in 401 .. 500 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_500, popularType)
            }
            in 501 .. 600 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_600, popularType)
            }
            in 601 .. 700 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_700, popularType)
            }
            in 701 .. 800 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_800, popularType)
            }
            in 801 .. 900 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_900, popularType)
            }
            in 901 .. 1000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1000, popularType)
            }
            in 1001 .. 1100 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1100, popularType)
            }
            in 1101 .. 1200 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1200, popularType)
            }
            in 1201 .. 1300 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1300, popularType)
            }
            in 1301 .. 1400 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1400, popularType)
            }
            in 1401 .. 1500 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1500, popularType)
            }
            in 1501 .. 1600 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1600, popularType)
            }
            in 1601 .. 1700 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1700, popularType)
            }
            in 1701 .. 1800 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1800, popularType)
            }
            in 1801 .. 1900 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1900, popularType)
            }
            in 1901 .. 2000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2000, popularType)
            }
            in 2001 .. 2100 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2100, popularType)
            }
            in 2101 .. 2200 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2200, popularType)
            }
            in 2201 .. 2300 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2300, popularType)
            }
            in 2301 .. 2400 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2400, popularType)
            }
            in 2401 .. 2500 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2500, popularType)
            }
            in 2501 .. 2600 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2600, popularType)
            }
            in 2601 .. 2700 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2700, popularType)
            }
            in 2701 .. 2800 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2800, popularType)
            }
            in 2801 .. 2900 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2900, popularType)
            }
            in 2901 .. 3000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3000, popularType)
            }
            in 3001 .. 3100 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3100, popularType)
            }
            in 3101 .. 3200 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3200, popularType)
            }
            in 3201 .. 3300 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3300, popularType)
            }
            in 3301 .. 3400 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3400, popularType)
            }
            in 3401 .. 3500 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3500, popularType)
            }
            in 3501 .. 3600 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3600, popularType)
            }
            in 3601 .. 3700 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3700, popularType)
            }
            in 3701 .. 3800 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3800, popularType)
            }
            in 3801 .. 3900 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3900, popularType)
            }
            in 3901 .. 3999 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_4000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_4000, popularType)
            }
            else -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_MORE_4000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_MORE_4000, popularType)
            }
        }

        // 소매가평균 - 차량 테이블 데이터 (인기 , 국산) * (소매가평균 / 테이블 구간 평균)

        logger.info { "국산여부 ${carManufacturer.countryType!!}" }
        logger.info { "인기차종 ${popularType}" }
        logger.info { "소매가 평균 $retailAvg" }
        logger.info { "테이블 구간 가격 i $tableValue" }
        logger.info { "${retailAvg.toDouble()} - ${tableValue.toDouble()} * (${retailAvg.toDouble()} / ${tableAvg.toDouble()})" }

        var guidePrice = (retailAvg.toDouble() - tableValue.toDouble() * (retailAvg.toDouble() / tableAvg.toDouble()))
        //var guidePrice = iValue - (tableAvg * (iValue / avePrice))
        logger.info { "guide Price $guidePrice" }
        logger.info { "반올림 이전 guide Price $guidePrice" }
        guidePrice = round(guidePrice).toDouble()
        logger.info { "반올림 이후 guide Price $guidePrice" }
        logger.info { "final guide Price $guidePrice" }
        return guidePrice.toLong() * 10000
    }


    fun getGuidePrice(guidePriceVM: GuidePriceCreateVM, storedGuide: Guide): Long {
        //TODO GUIDE PRICE 계산
        // 1. 소매가 4개 평균
        val retailAvg: Int  // 소매가 4개 평균
        val bidingAvg: Int  // 경매장가 2개 평균
        var retailSum = 0
        var retailCount = 0
        var bidingSum = 0
        var bidingCount = 0
        guidePriceVM.priceList?.forEach {
            if(it.marketType == MarketType.RETAIL) {
                retailSum += it.price!!.toInt() * 10000
                if(it.price!!.toInt() > 0) {
                    retailCount++
                }
            } else if(it.marketType == MarketType.BIDING) {
                bidingSum += it.price!!.toInt() * 10000
                if(it.price!!.toInt() > 0) {
                    bidingCount++
                }
            }
        }

        retailAvg = if(retailCount == 0) { 0 } else { retailSum / retailCount }
        bidingAvg = if(bidingCount == 0) { 0 } else { bidingSum / bidingCount }

        val retailConstPrice = guidePriceConfigRepository.findByPriceConfigType(PriceConfigType.RETAIL_CONST)
        val retailConst = retailConstPrice?.value?.toDouble()

        val iValue = ((retailAvg + (bidingAvg / retailConst!!) / 2) ).toLong()

        val carClass = carClassRepository.findById(storedGuide.carClassId!!).orElseThrow { CommException("not found carClass") }

        val carManufacturer = carManufacturerRepository.findById(storedGuide.carManufacturerId!!).orElseThrow { CommException("not found carManufacturer") }

        /*
        val avgPrice = guidePriceConfigRepository.findByCountryTypeAndPriceConfigType(
            carManufacturer.countryType!!,
            PriceConfigType.AVERAGE
        )
         */
        //avePrice = avgPrice?.value?.toLong()!! * 10000

        logger.info { "구간 가격 i $iValue" }
        logger.info { "국산여부 ${carManufacturer.countryType!!}" }
        logger.info { "인기차종 ${carClass.popularType!!}" }

        var tableAvg: Double
        var tableValue: Double
        when(retailAvg) {
            in 0..1000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_100, carClass.popularType!!)
            }
            in 1000001 .. 2000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_200, carClass.popularType!!)
            }
            in 2000001 .. 3000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_300, carClass.popularType!!)
            }
            in 3000001 .. 4000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_400, carClass.popularType!!)
            }
            in 4000001 .. 5000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_500, carClass.popularType!!)
            }
            in 5000001 .. 6000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_600, carClass.popularType!!)
            }
            in 6000001 .. 7000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_700, carClass.popularType!!)
            }
            in 7000001 .. 8000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_800, carClass.popularType!!)
            }
            in 8000001 .. 9000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_900, carClass.popularType!!)
            }
            in 9000001 .. 10000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1000, carClass.popularType!!)
            }
            in 10000001 .. 11000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1100, carClass.popularType!!)
            }
            in 11000001 .. 12000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1200, carClass.popularType!!)
            }
            in 12000001 .. 13000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1300, carClass.popularType!!)
            }
            in 13000001 .. 14000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1400, carClass.popularType!!)
            }
            in 14000001 .. 15000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1500, carClass.popularType!!)
            }
            in 15000001 .. 16000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1600, carClass.popularType!!)
            }
            in 16000001 .. 17000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1700, carClass.popularType!!)
            }
            in 17000001 .. 18000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1800, carClass.popularType!!)
            }
            in 18000001 .. 19000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_1900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_1900, carClass.popularType!!)
            }
            in 19000001 .. 20000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2000, carClass.popularType!!)
            }
            in 20000001 .. 21000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2100, carClass.popularType!!)
            }
            in 21000001 .. 22000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2200, carClass.popularType!!)
            }
            in 22000001 .. 23000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2300, carClass.popularType!!)
            }
            in 23000001 .. 24000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2400, carClass.popularType!!)
            }
            in 24000001 .. 25000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2500, carClass.popularType!!)
            }
            in 25000001 .. 26000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2600, carClass.popularType!!)
            }
            in 26000001 .. 27000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2700, carClass.popularType!!)
            }
            in 27000001 .. 28000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2800, carClass.popularType!!)
            }
            in 28000001 .. 29000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_2900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_2900, carClass.popularType!!)
            }
            in 29000001 .. 30000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3000, carClass.popularType!!)
            }
            in 30000001 .. 31000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3100, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3100, carClass.popularType!!)
            }
            in 31000001 .. 32000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3200, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3200, carClass.popularType!!)
            }
            in 32000001 .. 33000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3300, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3300, carClass.popularType!!)
            }
            in 33000001 .. 34000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3400, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3400, carClass.popularType!!)
            }
            in 34000001 .. 35000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3500, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3500, carClass.popularType!!)
            }
            in 35000001 .. 36000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3600, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3600, carClass.popularType!!)
            }
            in 36000001 .. 37000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3700, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3700, carClass.popularType!!)
            }
            in 37000001 .. 38000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3800, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3800, carClass.popularType!!)
            }
            in 38000001 .. 39000000 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_3900, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_3900, carClass.popularType!!)
            }
            in 39000001 .. 39999999 -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_4000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_4000, carClass.popularType!!)
            }
            else -> {
                tableAvg = getTablePrice(carManufacturer.countryType!!, PriceConfigType.AVG_T_MORE_4000, null)
                tableValue = getTablePrice(carManufacturer.countryType!!, PriceConfigType.T_MORE_4000, carClass.popularType!!)
            }
        }

        //tablePrice *= 10000

        tableValue *= 10000
        tableAvg *= 10000

        logger.info { "소매가 평균 $retailAvg" }
        logger.info { "경매가 평균 $bidingAvg" }
        logger.info { "테이블 구간 가격 i $tableValue" }
        logger.info { "테이블 구간 평균 i $tableAvg" }
        //logger.info { "해당 차량 평균 가 $avePrice" }



        var guidePrice = retailAvg - tableValue * (retailAvg / tableAvg)

        logger.info { "$retailAvg - $tableValue * (${retailAvg} / ${tableAvg}) = $guidePrice" }

        /*
        if(bidingAvg < guidePrice) {
            guidePrice = bidingAvg.toDouble()
        }
         */

        //var guidePrice = iValue - (tableAvg * (iValue / avePrice))
        logger.info { "guide Price $guidePrice" }
        var temp = guidePrice.toDouble() / 10000
        logger.info { "반올림 이전 guide Price $temp" }
        temp = round(temp).toDouble()
        logger.info { "반올림 이후 guide Price $temp" }
        guidePrice = (temp * 10000)
        logger.info { "final guide Price $guidePrice" }
        return guidePrice.toLong()
    }

    private fun getTablePrice(countryType: CountryType, priceConfigType: PriceConfigType, popularType: PopularType?): Double {
        return if(popularType == null){
            guidePriceConfigRepository.findByCountryTypeAndPriceConfigType(
                countryType,
                priceConfigType
            )?.value?.toDouble() ?: 0.0
        } else {
            guidePriceConfigRepository.findByCountryTypeAndPriceConfigTypeAndPopularType(
                countryType,
                priceConfigType,
                popularType
            )?.value?.toDouble() ?: 0.0
        }
    }

}