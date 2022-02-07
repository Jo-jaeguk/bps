package com.mobilityk.core.service

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.CommentType
import com.mobilityk.core.domain.ConfigType
import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.DamageType
import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuideAddImg
import com.mobilityk.core.domain.GuideCheckList
import com.mobilityk.core.domain.GuideComment
import com.mobilityk.core.domain.GuideCommentSearchOption
import com.mobilityk.core.domain.GuideDamageCheck
import com.mobilityk.core.domain.GuideOption
import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.domain.ImgType
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.TireWearType
import com.mobilityk.core.domain.buyandsell.BuyAndSell
import com.mobilityk.core.domain.buyandsell.Sell
import com.mobilityk.core.dto.DashBoardDTO
import com.mobilityk.core.dto.DashBoardGuide
import com.mobilityk.core.dto.GuideAddImgDTO
import com.mobilityk.core.dto.GuideCheckListDTO
import com.mobilityk.core.dto.GuideCheckListMapper
import com.mobilityk.core.dto.GuideCommentDTO
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.GuideOptionDTO
import com.mobilityk.core.dto.GuidePriceDTO
import com.mobilityk.core.dto.UploadDTO
import com.mobilityk.core.dto.api.guide.DamageInfo
import com.mobilityk.core.dto.api.guide.GuideCommentForAdminDTO
import com.mobilityk.core.dto.api.guide.GuideCommentVM
import com.mobilityk.core.dto.api.guide.GuideCreateVM
import com.mobilityk.core.dto.api.guide.GuideInspectSaveVM
import com.mobilityk.core.dto.api.guide.GuideInspectVM
import com.mobilityk.core.dto.api.guide.GuideUpdateVM
import com.mobilityk.core.dto.buyandsell.SellDTO
import com.mobilityk.core.dto.mapper.GuideAddImgMapper
import com.mobilityk.core.dto.mapper.GuideCommentMapper
import com.mobilityk.core.dto.mapper.GuideMapper
import com.mobilityk.core.dto.mapper.GuideOptionMapper
import com.mobilityk.core.dto.mapper.GuidePriceMapper
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.BranchRepository
import com.mobilityk.core.repository.CarOptionRepository
import com.mobilityk.core.repository.CheckListConfigRepository
import com.mobilityk.core.repository.ConfigRepository
import com.mobilityk.core.repository.DamageCheckConfigRepository
import com.mobilityk.core.repository.GuideAddImgRepository
import com.mobilityk.core.repository.GuideCheckListRepository
import com.mobilityk.core.repository.GuideCommentRepository
import com.mobilityk.core.repository.GuideDamageCheckRepository
import com.mobilityk.core.repository.GuideOptionRepository
import com.mobilityk.core.repository.GuidePriceRepository
import com.mobilityk.core.repository.GuideRepository
import com.mobilityk.core.repository.GuideTempOptionRepository
import com.mobilityk.core.repository.GuideTempRepository
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.buyandsell.BuyAndSellRepository
import com.mobilityk.core.repository.buyandsell.SellRepository
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.servlet.http.HttpServletRequest

@Service
class GuideService(
    private val guideRepository: GuideRepository,
    private val guideMapper: GuideMapper,
    private val memberRepository: MemberRepository,
    private val guideCommentRepository: GuideCommentRepository,
    private val guideCommentMapper: GuideCommentMapper,
    private val kakaoTalkService: KakaoTalkService,
    private val memberMapper: MemberMapper,
    private val guideTempRepository: GuideTempRepository,
    private val branchRepository: BranchRepository,
    private val guidePriceRepository: GuidePriceRepository,
    private val guideAddImgRepository: GuideAddImgRepository,
    private val guideTempOptionRepository: GuideTempOptionRepository,
    private val guideOptionRepository: GuideOptionRepository,
    private val buyAndSellRepository: BuyAndSellRepository,
    private val carOptionRepository: CarOptionRepository,
    private val configRepository: ConfigRepository,
    private val damageCheckConfigRepository: DamageCheckConfigRepository,
    private val newCarPriceService: NewCarPriceService,
    private val guideCheckListRepository: GuideCheckListRepository,
    private val guideDamageCheckRepository: GuideDamageCheckRepository,
    private val checkListConfigRepository: CheckListConfigRepository,
    private val sellRepository: SellRepository,
    private val guideAddImgMapper: GuideAddImgMapper,
    private val guidePriceMapper: GuidePriceMapper,
    private val guideOptionMapper: GuideOptionMapper,
    private val guideCheckListMapper: GuideCheckListMapper,
    private val uploadService: UploadService
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun getGuideTotalCountByMemberId(memberId: Long): Long {
        return guideRepository.count()
    }

    @Transactional
    fun getGuideAggregationBySearchOption(searchOption: GuideSearchOption): MutableMap<String, Long> {
        return guideRepository.getAggregation(searchOption)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getDashBoard(): DashBoardDTO {

        val dashBoardDTO = DashBoardDTO()
        var searchOption = GuideSearchOption(
            startedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)),
            endedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)),
            guideStatusList = listOf(GuideStatus.REQUEST)
        )
        var request_today_map = guideRepository.getAggregation(searchOption)
        dashBoardDTO.todayGuideRequestCnt = request_today_map["count"]

        searchOption = GuideSearchOption(
            startedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)),
            endedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)),
            guideStatusList = listOf(GuideStatus.FINISH)
        )
        var finish_today_map = guideRepository.getAggregation(searchOption)
        dashBoardDTO.todayGuideFinishCnt = finish_today_map["count"]


        searchOption = GuideSearchOption(
            startedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)),
            endedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)),
            guideStatusList = listOf(GuideStatus.BUY)
        )
        var buy_today_map = guideRepository.getAggregation(searchOption)
        dashBoardDTO.todayGuideBuyCnt = buy_today_map["count"]
        dashBoardDTO.todayGuideBuyMoney = buy_today_map["money"]

        searchOption = GuideSearchOption(
            startedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)),
            endedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)),
            guideStatusList = listOf(GuideStatus.SELLING)
        )
        var selling_today_map = guideRepository.getAggregation(searchOption)
        dashBoardDTO.todayGuideSellingCnt = selling_today_map["count"]

        searchOption = GuideSearchOption(
            startedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)),
            endedAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)),
            guideStatusList = listOf(GuideStatus.SELL)
        )
        var sell_today_map = guideRepository.getAggregation(searchOption)
        dashBoardDTO.todayGuideSellCnt = sell_today_map["count"]
        dashBoardDTO.todayGuideSellMoney = sell_today_map["money"]

        // monthly
        val weeklyList = mutableListOf<DashBoardGuide>()

        searchOption.guideStatus = null

        // 타겟 날짜의 주 차수 기준의 날짜 리스트 계산
        val result = CommonUtil.getWeekDateTimeList(LocalDateTime.now())
        result.sortedBy { it.index }
        result.forEach { weekDateTime ->
            searchOption.startedAt = weekDateTime.startedAt
            searchOption.endedAt = weekDateTime.endedAt
            val guideWeekList = guideRepository.findAllBySearch(searchOption)

            val requestGuides = guideWeekList.filter { it.guideStatus == GuideStatus.REQUEST }
            val buyGuides = guideWeekList.filter { it.guideStatus == GuideStatus.BUY }
            val sellingGuides = guideWeekList.filter { it.guideStatus == GuideStatus.SELLING }
            val sellGuides = guideWeekList.filter { it.guideStatus == GuideStatus.SELL }
            val finishGuides = guideWeekList.filter { it.guideStatus == GuideStatus.FINISH }

            weeklyList.add(
                DashBoardGuide(
                    index = weekDateTime.index,
                    requestCnt = requestGuides.size.toLong(),
                    buyCnt = buyGuides.size.toLong(),
                    buyMoney = buyGuides.sumOf { it.price!! },
                    finishCnt = finishGuides.size.toLong(),
                    sellCnt = sellGuides.size.toLong(),
                    sellMoney = sellGuides.sumOf { it.price!! },
                    sellingCnt = sellingGuides.size.toLong()
                )
            )
        }

        dashBoardDTO.weekly = weeklyList
        dashBoardDTO.totalSellMoney = guideRepository.getTotalSellMoney()

        return dashBoardDTO
    }

    @Transactional
    fun findAllBySearchOption(searchOption: GuideSearchOption, pageable: Pageable): Page<GuideDTO> {
        return guideRepository.findAllBySearch(searchOption, pageable)
    }


    @Transactional
    fun findAllAdditionalImagesById(guideId: Long): List<GuideAddImgDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { throw CommException("not found guide") }
        return guideAddImgMapper.toDtoList(guideAddImgRepository.findAllByGuideAndImgTypeNotIn(guide, listOf(ImgType.DAMAGE)))
    }

    @Transactional
    fun findAllDamageImagesById(guideId: Long): List<GuideAddImgDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { throw CommException("not found guide") }
        return guideAddImgMapper.toDtoList(guideAddImgRepository.findAllByGuideAndImgType(guide, ImgType.DAMAGE))
    }

    @Transactional
    fun findAllBySearchExcel(searchOption: GuideSearchOption, pageable: Pageable): Page<GuideDTO> {
        return guideRepository.findAllBySearchExcel(searchOption, pageable)
    }


    @Transactional
    fun getGuidePrices(guideId: Long): List<GuidePriceDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guidePriceMapper.toDtoList(guidePriceRepository.findAllByGuide(guide))
    }



    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun findByGuideId(id: Long, memberId: Long): GuideDTO {
        val guide = guideRepository.findByGuideId(id) ?: throw CommException("not found guide")
        if(guide.memberId != memberId) throw CommException("no permission")

        if(guide.adminId != null) {
            // 제출자가 존재한다면
            val memberOpt = memberRepository.findById(guide.adminId!!)
            if(memberOpt.isPresent) {
                val admin = memberOpt.get();
                guide.adminId = admin.id
                guide.adminName = admin.name
                guide.adminPhone = admin.phone
                guide.adminPosition = admin.position
            }
        }
        return guide
    }

    @Transactional
    fun findByGuideIdForAdmin(id: Long): GuideDTO {
        return guideRepository.findByGuideId(id) ?: throw CommException("not found guide")
    }

    @Transactional
    fun getGuideCarOptions(guideId: Long): List<GuideOptionDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guideOptionMapper.toDtoList(guideOptionRepository.findAllByGuide(guide))
    }

    @Transactional
    fun updateRetailAvgPrice(guideId: Long): GuideDTO {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }

        val guidePriceList = guidePriceRepository.findAllByGuide(guide)

        var retailCnt = 0
        var bidCnt = 0
        var retailSum = 0L
        var bidSum = 0L
        guidePriceList.forEach { guidePrice ->
            if(guidePrice.marketType == MarketType.RETAIL) {
                if(guidePrice.price != null && guidePrice.price!! > 0) {
                    retailCnt++
                    retailSum += guidePrice.price!!
                }
            } else {
                if(guidePrice.price != null && guidePrice.price!! > 0) {
                    bidCnt++
                    bidSum += guidePrice.price!!
                }
            }
        }
        if(retailSum > 0) {
            guide.retailAvgPrice = retailSum / retailCnt
        }
        if(bidSum > 0) {
            guide.bidAvgPrice = bidSum / bidCnt
        }
        return guideMapper.toDto(guideRepository.save(guide))
    }

    @Transactional
    fun getGuideCheckList(guideId: Long): List<GuideCheckListDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guideCheckListMapper.toDtoList(guideCheckListRepository.findAllByGuide(guide))
    }


    @Transactional
    fun findInspectByGuideId(guideId: Long): GuideDTO {
        return guideRepository.findByGuideId(guideId) ?: throw CommException("not found guide")
    }

    @Transactional
    fun deleteGuideOptions(guideId: Long) {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        guideOptionRepository.deleteAllByGuide(guide)
    }

    @Transactional
    fun deleteGuideCheckList(guideId: Long) {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        guideCheckListRepository.deleteAllByGuide(guide)
    }

    @Transactional
    fun deleteGuideDamages(guideId: Long) {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        guideDamageCheckRepository.deleteAllByGuide(guide)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun saveInpect(guideId: Long, inspectSaveVM: GuideInspectSaveVM, writerId: Long): GuideDTO {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        guide.saveInspect(inspectSaveVM)

        inspectSaveVM.carOptionIdList?.forEach { carOptionId ->
            val carOption = carOptionRepository.findById(carOptionId).orElseThrow { CommException("not found car option") }
            guideOptionRepository.save(
                GuideOption(
                    guide = guide,
                    option = carOption.option,
                    writerId = writerId
                )
            )
        }

        inspectSaveVM.checkListIds?.forEach { checkListId ->
            val checkListConfig = checkListConfigRepository.findById(checkListId).orElseThrow { CommException("not found check list") }
            guideCheckListRepository.save(
                GuideCheckList(
                    guide = guide,
                    price = checkListConfig.price,
                    question = checkListConfig.question
                )
            )
        }

        guideDamageCheckRepository.deleteAllByGuide(guide)
        inspectSaveVM.minus?.damageList?.forEach { damageInfo ->
            guideDamageCheckRepository.save(
                GuideDamageCheck(
                    guide = guide,
                    price = damageInfo.price,
                    carType = damageInfo.carType,
                    damageLocation = damageInfo.damageLocationEnum,
                    damageType = damageInfo.damageType
                )
            )
        }

        return guideMapper.toDto(guideRepository.save(guide))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createGuide(
        guideCreateVM: GuideCreateVM,
        memberId: Long
    ): GuideDTO {
        val memberOpt = memberRepository.findById(memberId)
        if(memberOpt.isEmpty) throw CommException("not found user")
        /*
        val carManufacturer = carManufacturerRepository.findById(guideCreateVM.carManufacturerId!!).orElseThrow { CommException("not found carmanufacturer") }
        val carClass = carClassRepository.findById(guideCreateVM.carClassId!!).orElseThrow { CommException("not found car class") }
        //val carModel = carClass.carModel
        val carColor = carColorRepository.findById(guideCreateVM.carColorId!!).orElseThrow { CommException("not found car color") }
        val carTrim = carTrimRepository.findById(guideCreateVM.carTrimId!!).orElseThrow { CommException("not found car trim") }
         */

        val member = memberOpt.get()

        val guideTemp = guideTempRepository.findById(guideCreateVM.guideTempId!!).orElseThrow { CommException("not found") }
        val guide = Guide(
            memberId = memberId,
            carModel = guideTemp.carModel,
            carModelDetail = guideTemp.carModelDetail,
            carModelId = guideTemp.carModelId,
            price = 0L,
            guideStatus = GuideStatus.REQUEST,
            country = Country.KOREA,
            customerName = guideTemp.customerName,
            customerContact = guideTemp.customerContact,
            carNumber = guideTemp.carNumber,
            carTrim = guideTemp.carTrim,
            carTrimId = guideTemp.carTrimId,
            carManufacturer = guideTemp.carManufacturer,
            carManufacturerId = guideTemp.carManufacturerId,
            carClassName = guideTemp.carClassName,
            carClassId = guideTemp.carClassId,
            carMadedYear = guideTemp.carMadedYear,
            carMadedMonth = guideTemp.carMadedMonth,
            carLocation = "",
            carDisplacement = guideTemp.carDisplacement,
            accident = guideTemp.accident,
            fuelType = guideTemp.fuelType,
            mileage = guideTemp.mileage,
            motorType = guideTemp.motorType,
            option1 = guideTemp.option1,
            option2 = guideTemp.option2,
            option3 = guideTemp.option3,
            carColor = guideTemp.carColor,
            sendYn = EnumYn.N,
            inspectedYn = EnumYn.N,
            finalBuyPriceSendYn = EnumYn.N,
            finalMinusPrice = 0,
            lossPrice = 0,
            finalBuyPrice = 0,
            checkMinusPrice = 0,
            retailAvgPrice = 0,
            bidAvgPrice = 0
        )
        //guideTempRepository.delete(guideTemp)
        guideTempRepository.deleteAllByCreatedAtBefore(LocalDateTime.now().minusDays(2))

        val findByMaxGuideIndex = guideRepository.findByMaxGuideIndex(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))
        )
        val guideIndex = if(findByMaxGuideIndex.isNullOrEmpty()) {
            0
        } else {
            findByMaxGuideIndex[0].guideIndex!! + 1
        }
        val branchNumber = if(member.manageBranch.isNullOrEmpty()) {
            "00"
        } else {
            val branch = branchRepository.findByBranch(member.manageBranch!!)
            branch.get().branchNumber
        }

        val saved = guideRepository.save(guide)

        logger.info { "guide index $guideIndex" }

        saved.makeSerial(
            memberDTO = memberMapper.toDto(member),
            branchNumber = branchNumber!!,
            guideIndex = guideIndex
        )

        logger.info { "guide serialNumber ${saved.serial}" }

        member.incrementGuideCount()

        val guideImages = mutableListOf<GuideAddImg>(
            GuideAddImg(
                guide = saved,
                imgType = ImgType.GAUGE,
                imgUrl = guideTemp.gaugeImgUrl
            ),
            GuideAddImg(
                guide = saved,
                imgType = ImgType.FRONT,
                imgUrl = guideTemp.frontImgUrl
            ),
            GuideAddImg(
                guide = saved,
                imgType = ImgType.BACK,
                imgUrl = guideTemp.backImgUrl
            ),
            GuideAddImg(
                guide = saved,
                imgType = ImgType.LEFT,
                imgUrl = guideTemp.leftImgUrl
            ),
            GuideAddImg(
                guide = saved,
                imgType = ImgType.RIGHT,
                imgUrl = guideTemp.rightImgUrl
            ),
        )
        guideAddImgRepository.saveAll(guideImages)


        val guideTempOptions = guideTempOptionRepository.findAllByGuideTempIdAndMemberId(guideCreateVM.guideTempId!!, memberId)
        logger.info { "guideTempOptions -> $guideTempOptions" }
        val guideOptions = mutableListOf<GuideOption>()
        guideTempOptions.forEach { guideTempOption ->
            guideOptions.add(
                GuideOption(
                    guide = saved,
                    option = guideTempOption.option,
                    writerId = memberId
                )
            )
        }
        if(!guideOptions.isNullOrEmpty()) {
            guideOptionRepository.saveAll(guideOptions)
        }

        return guideMapper.toDto(
            saved
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun resetDamageCheck(guideId: Long) {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        guide.inspectTopImgUrl = "/assets/img/img-car_top.png"
        guide.inspectSideImgUrl = "/assets/img/img-car_side.png"
        guide.inspectBottomImgUrl = "/assets/img/img-car_under.png"
        guide.damageMinusPrice = 0L
        guideDamageCheckRepository.deleteAllByGuide(guide)
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun requestBuy(guideId: Long): GuideDTO {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        guide.guideStatus = GuideStatus.BUY_REQUEST
        val buyAndSellOpt = buyAndSellRepository.findByGuideId(guide.id!!)
        if(buyAndSellOpt.isEmpty) {
            val buyandSell = BuyAndSell()
            buyandSell.create(guide.id!!)
            buyAndSellRepository.save(buyandSell)
        }
        return guideMapper.toDto(guideRepository.save(guide))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getInspectMinusPrice(guideInspectVM: GuideInspectVM): GuideInspectVM {

        val guide = guideRepository.findById(guideInspectVM.guideId!!).orElseThrow { CommException("not found guide") }

        val guideSendPrice = guide.sendPrice
        var basePrice = 0L
        when(guideInspectVM.carType) {
            CarTypeEnum.SMALL -> {
                val config = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_SMALL_PRICE).orElseThrow { CommException("기준가 설정이 안되어 있습니다.") }
                basePrice = config.value?.toLong()!!
            }
            CarTypeEnum.MID -> {
                val config = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_MID_PRICE).orElseThrow { CommException("기준가 설정이 안되어 있습니다.") }
                basePrice = config.value?.toLong()!!
            }
            CarTypeEnum.BIG -> {
                val config = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_BIG_PRICE).orElseThrow { CommException("기준가 설정이 안되어 있습니다.") }
                basePrice = config.value?.toLong()!!
            }
            CarTypeEnum.FOREIGN -> {
                val config = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_FOREIGN_PRICE).orElseThrow { CommException("기준가 설정이 안되어 있습니다.") }
                basePrice = config.value?.toLong()!!
            }
            CarTypeEnum.RV -> {
                val config = configRepository.findByConfigType(ConfigType.DAMAGE_BASE_RV_PRICE).orElseThrow { CommException("기준가 설정이 안되어 있습니다.") }
                basePrice = config.value?.toLong()!!
            }
        }

        var resultVM: GuideInspectVM = GuideInspectVM()
        resultVM.checkList = mutableListOf()
        resultVM.damageList = mutableListOf()

        guideInspectVM.checkListIds?.forEach { id ->
            val checkListOpt = checkListConfigRepository.findById(id)
            if(checkListOpt.isPresent) {
                val checkList = checkListOpt.get()
                val guideCheckList = GuideCheckListDTO()
                guideCheckList.price = checkList.price
                guideCheckList.question = checkList.question
                resultVM.checkList?.add(guideCheckList)
            }
        }

        guideInspectVM.damageList?.forEach { damageInfo ->
            val damageConfigOpt = damageCheckConfigRepository.findByCarTypeAndDamageLocation(
                damageInfo.carType!!,
                damageInfo.damageLocationEnum!!
            )

            if(damageConfigOpt.isPresent) {
                val resDamageInfo = DamageInfo()

                resDamageInfo.carType = damageInfo.carType
                resDamageInfo.damageType = damageInfo.damageType
                resDamageInfo.damageLocationEnum = damageInfo.damageLocationEnum

                val damageConfig = damageConfigOpt.get()
                logger.info { "guide sendPrice ${guideSendPrice}" }
                logger.info { "basePrice ${basePrice}" }
                when(damageInfo.damageType) {
                    DamageType.GI_GYOHWAN -> {
                        logger.info { "giGyoHwanPrice ${damageConfig.giGyoHwanPrice!!}" }
                        if(guide.sendPrice!! <= basePrice) {
                            resDamageInfo.price = (guideSendPrice!!.toDouble() / basePrice.toDouble() * damageConfig.giGyoHwanPrice!!).toLong()
                        } else {
                            resDamageInfo.price = damageConfig.giGyoHwanPrice!!
                        }
                    }
                    DamageType.GI_PANGUM -> {
                        if(guide.sendPrice!! <= basePrice) {
                            resDamageInfo.price = (guideSendPrice!!.toDouble() / basePrice.toDouble() * damageConfig.giPangumPrice!!).toLong()
                        } else {
                            resDamageInfo.price = damageConfig.giPangumPrice!!
                        }
                    }
                    DamageType.GI_DOSAEK -> {
                        if(guide.sendPrice!! <= basePrice) {
                            resDamageInfo.price = (guideSendPrice!!.toDouble() / basePrice.toDouble() * damageConfig.giDosaekPrice!!).toLong()
                        } else {
                            resDamageInfo.price = damageConfig.giDosaekPrice!!
                        }

                    }
                    DamageType.NEED_PANGUM -> {
                        if(guide.sendPrice!! <= basePrice) {
                            resDamageInfo.price = (guideSendPrice!!.toDouble() / basePrice.toDouble() * damageConfig.needPangumPrice!!).toLong()
                        } else {
                            resDamageInfo.price = damageConfig.needPangumPrice!!
                        }
                    }
                    DamageType.NEED_DOSAEK -> {
                        if(guide.sendPrice!! <= basePrice) {
                            resDamageInfo.price = (guideSendPrice!!.toDouble() / basePrice.toDouble() * damageConfig.needDosaekPrice!!).toLong()
                        } else {
                            resDamageInfo.price = damageConfig.needDosaekPrice!!
                        }
                    }
                    DamageType.NEED_GYOHWAN -> {
                        if(guide.sendPrice!! <= basePrice) {
                            resDamageInfo.price = (guideSendPrice!!.toDouble() / basePrice.toDouble() * damageConfig.needGyoHwanPrice!!).toLong()
                        } else {
                            resDamageInfo.price = damageConfig.needGyoHwanPrice!!
                        }
                    }
                    DamageType.OTHER -> {
                        if(guide.sendPrice!! <= basePrice) {
                            resDamageInfo.price = (guideSendPrice!!.toDouble() / basePrice.toDouble() * damageConfig.otherPrice!!).toLong()
                        } else {
                            resDamageInfo.price = damageConfig.otherPrice!!
                        }
                    }
                }
                resultVM.damageList?.add(resDamageInfo)
            }
        }

        resultVM.tireWearType = guideInspectVM.tireWearType
        resultVM.tireWearPrice = guideInspectVM.tireWearPrice
        resultVM.tireWearReason = guideInspectVM.tireWearReason
        if(guideInspectVM.tireWearType == TireWearType.EA_0) {
            resultVM.tireWearCount = 0
        } else if(guideInspectVM.tireWearType == TireWearType.EA_1) {
            resultVM.tireWearCount = 1
        } else if(guideInspectVM.tireWearType == TireWearType.EA_2) {
            resultVM.tireWearCount = 2
        } else if(guideInspectVM.tireWearType == TireWearType.EA_3) {
            resultVM.tireWearCount = 3
        } else if(guideInspectVM.tireWearType == TireWearType.EA_4) {
            resultVM.tireWearCount = 4
        } else if(guideInspectVM.tireWearType == TireWearType.OTHER) {
            resultVM.tireWearCount = 0
        }

        logger.info { "damage info ${resultVM.damageList}" }

        return resultVM
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateGuide(
        id: Long,
        guideCreateVM: GuideCreateVM,
        memberId: Long
    ): GuideDTO {
        val member = memberRepository.findById(memberId)
        if(member.isEmpty) throw CommException("not found user")

        val stored = guideRepository.findById(id)
        if(stored.isEmpty) throw CommException("not found guide")

        val guide = stored.get()
        if(guide.memberId != member.get().id) {
            throw CommException("no permmission")
        }
        guide.updateGuide(guideCreateVM)
        return guideMapper.toDto(
            guideRepository.save(guide)
        )
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateGuideStatusAndCarLocation(
        id: Long,
        adminId: Long,
        guideStatus: GuideStatus,
        carLocation: String
    ): GuideDTO {
        val stored = guideRepository.findById(id)
        if(stored.isEmpty) throw CommException("not found guide")
        val guide = stored.get()
        val admin = memberRepository.findById(adminId).orElseThrow { CommException("not found admin") }
        guide.guideStatus = guideStatus
        guide.carLocation = carLocation
        guide.adminId = admin.id
        return guideMapper.toDto(
            guideRepository.save(guide)
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun modifyGuide(
        id: Long,
        adminId: Long,
        guideUpdateVM: GuideUpdateVM,
    ): GuideDTO {
        val stored = guideRepository.findById(id)
        if(stored.isEmpty) throw CommException("not found guide")
        val guide = stored.get()
        val admin = memberRepository.findById(adminId).orElseThrow { CommException("not found admin") }
        guide.guideStatus = guideUpdateVM.guideStatus
        guide.carLocation = guideUpdateVM.carLocation

        if(!admin.roles?.contains(ROLE.MASTER)!!) {
            if(guide.carMadedYear != guideUpdateVM.carMadedYear ||
                guide.carMadedMonth != guideUpdateVM.carMadedMonth ||
                guide.mileage != guideUpdateVM.mileage ||
                guide.accident != guideUpdateVM.accident ||
                guide.carColor != guideUpdateVM.carColor ||
                guide.carNumber != guideUpdateVM.carNumber
                ) {
                throw CommException("차량정보는 MASTER 만 수정 가능합니다.")
            }
        } else {
            guide.carMadedYear = guideUpdateVM.carMadedYear
            guide.carMadedMonth = guideUpdateVM.carMadedMonth
            guide.mileage = guideUpdateVM.mileage
            guide.accident = guideUpdateVM.accident
            guide.carColor = guideUpdateVM.carColor
            guide.carNumber = guideUpdateVM.carNumber
        }

        if(guideUpdateVM.newCarPrice != null) {
            guide.newCarPrice = guideUpdateVM.newCarPrice!! * 10000
            /*
            newCarPriceService.upsertNewCarPrice(
                carManufacturerName = guide.carManufacturer!!,
                carModelName = guide.carModel!!,
                carModelDetailName = guide.carModelDetail!!,
                carTrimName = guide.carTrim!!,
                price = guideUpdateVM.newCarPrice!!
            )
             */
        }


        //guide.option1 = guideUpdateVM.option1
        //guide.option2 = guideUpdateVM.option2
        //guide.option3 = guideUpdateVM.option3

        guide.adminId = admin.id

        if(guideUpdateVM.guideStatus == GuideStatus.BUY) {
            val increasePoint = 20000 * (guide.finalBuyPrice?.div(25000000)!!)
            val member = memberRepository.findById(guide.memberId!!).orElseThrow { CommException("not found user") }
            member.increasePoint(increasePoint)
            memberRepository.save(member)
        }


        if(guide.guideStatus == GuideStatus.BUY) {
            val buyAndSellOpt = buyAndSellRepository.findByGuideId(guide.id!!)
            val buyAndSell = if(buyAndSellOpt.isPresent) {
                buyAndSellOpt.get()
            } else {
                val buyandSell = BuyAndSell()
                buyandSell.create(guide.id!!)
                buyAndSellRepository.save(buyandSell)
            }
            buyAndSell.buyDate = LocalDateTime.now()

            val sellOpt = sellRepository.findAllByBuySellOrderByOrderIndexDesc(buyAndSell)
            if(sellOpt.isEmpty()) {
                sellRepository.save(
                    Sell(
                        buySell = buyAndSell,
                    hopePrice = 10000*(guide.retailAvgPrice!!), // 판매예상가는 기본으로 가이드의 소매평균가 //단위변경
                        orderIndex = 0,
                        otherCommission = 0L,
                        sellPrice = 0L,
                        incomeYn = EnumYn.N,
                        needDocumentYn = EnumYn.N,
                    )
                )
            }

        }

        return guideMapper.toDto(
            guideRepository.save(guide)
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun sendGuide(
        id: Long,
        adminId: Long,
        guideDTO: GuideDTO
    ): Unit {
        val stored = guideRepository.findById(id)

        if(stored.isEmpty) throw CommException("not found guide")
        val guide = stored.get()

        val member = memberRepository.findById(guide.memberId!!).orElseThrow { CommException("not found user") }
        val admin = memberRepository.findById(adminId).orElseThrow { CommException("not found user") }

        if(!admin.roles?.contains(ROLE.MASTER)!! && !admin.roles?.contains(ROLE.ADMIN)!!) {
            throw CommException("가이드 전송이 불가능한 권한입니다.")
        }

        if(guide.guideStatus != GuideStatus.FINISH) {
            throw CommException("가이드 완료 상태에서만 가이드 전송이 가능합니다.")
        }

        guide.adminId = adminId
        guide.adminName = admin.name
        guide.adminPhone = admin.phone
        guide.adminPosition = admin.position
        guide.sendYn = EnumYn.Y
        guide.sendPrice = guideDTO.sendPrice

        val guideOptions = guideOptionRepository.findAllByGuide(guide)

        kakaoTalkService.sendKakaoTalk(
            targetMemberDTO = memberMapper.toDto(member),
            sendPhone = member.phone!!,
            titleKey = "guide.receive.title",
            messageKey = "guide.receive.body",
            templateCodeKey = "guide.receive.template",
            btnUrlKey = null,
            btnTextKey = null,
            messageList = CommonUtil.getGuideKakaoUserSendMessage(
                guideDTO = guideMapper.toDto(guide),
                memberDTO = memberMapper.toDto(member),
                adminDTO = memberMapper.toDto(admin),
                guideOptions = guideOptionMapper.toDtoList(guideOptions)
            ),
            urlList = null
        )
           // 고객에게 전송기능
        kakaoTalkService.sendKakaoTalk(

            targetMemberDTO = memberMapper.toDto(member),
            sendPhone = guide.customerContact!!,
            titleKey = "guide.receive.title",
            messageKey = "guide.receive.body",
            templateCodeKey = "guide.receive.template",
            btnUrlKey = null,
            btnTextKey = null,
            messageList = CommonUtil.getGuideKakaoUserSendMessage(
                guideDTO = guideMapper.toDto(guide),
                memberDTO = memberMapper.toDto(member),
                adminDTO = memberMapper.toDto(admin),
                guideOptions = guideOptionMapper.toDtoList(guideOptions)
            ),
            urlList = null
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteGuideByUser(
        id: Long,
        memberId: Long
    ): Unit {
        val member = memberRepository.findById(memberId)
        if(member.isEmpty) throw CommException("not found user")

        val stored = guideRepository.findById(id)
        if(stored.isEmpty) throw CommException("not found guide")

        val guide = stored.get()
        if(guide.memberId != member.get().id) {
            throw CommException("no permmission")
        }
        guideRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteGuideByAdmin(
        id: Long,
        adminId: Long
    ): Unit {
        val adminOpt = memberRepository.findById(adminId)
        if(adminOpt.isEmpty) throw CommException("not found admin")

        val admin = adminOpt.get()
        if(!admin.roles?.contains(ROLE.MASTER)!!) {
            throw CommException("마스터 권한만 삭제 가능합니다.")
        }

        guideRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createGuideComment(guideId: Long, writerId: Long, guideCommentVM: GuideCommentVM): GuideCommentDTO{
        val guide = guideRepository.findById(guideId)
        if(guide.isEmpty) throw CommException("not found guide")
        val storedGuide = guide.get()
        return guideCommentMapper.toDto(
            guideCommentRepository.save(
                GuideComment(
                    writerId = writerId,
                    guide = storedGuide,
                    commentType = guideCommentVM.commentType,
                    body = guideCommentVM.body
                )
            )
        ).copy(member = memberMapper.toDto(memberRepository.findById(writerId).get()))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun findAllComments(guideId: Long, commentType: CommentType): List<GuideCommentForAdminDTO> {
        val guide = guideRepository.findById(guideId)
        if(guide.isEmpty) throw CommException("not found guide")
        val searchOption = GuideCommentSearchOption(
            guide = guide.get(),
            commentType = commentType
        )
        return guideCommentRepository.findAllByGuideForAdmin(searchOption)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteInspect(guideId: Long): Unit {
        val guideOpt = guideRepository.findById(guideId)
        if(guideOpt.isEmpty) throw CommException("not found guide")
        val guide = guideOpt.get()
        guideAddImgRepository.deleteAllByGuide(guide)
        guide.deleteInspect()
        guideRepository.save(guide)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateInspect(guideId: Long, guideDTO: GuideDTO): Unit {
        val guideOpt = guideRepository.findById(guideId)
        if(guideOpt.isEmpty) throw CommException("not found guide")
        val guide = guideOpt.get()
        guide.newCarPrice = guideDTO.newCarPrice
        guideRepository.save(guide)
    }

    @Transactional
    fun getGuideImages(guideId: Long): List<GuideAddImgDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        return guideAddImgMapper.toDtoList(guideAddImgRepository.findAllByGuide(guide))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createGuideCarImages(
        image1: MultipartFile?,
        image2: MultipartFile?,
        image3: MultipartFile?,
        image4: MultipartFile?,
        image5: MultipartFile?,
        guideTempId: Long,
        memberId: Long,
        request: HttpServletRequest
    ): Unit {
        logger.info { image1?.originalFilename }
        logger.info { image2?.originalFilename }
        val guideTemp = guideTempRepository.findById(guideTempId).orElseThrow { CommException("not found temp guide") }
        if(!image1?.originalFilename.isNullOrEmpty()) {
            val gauge = uploadService.createUpload(memberId, image1, request)
            guideTemp.gaugeImgUrl = gauge.url
        }
        if(!image2?.originalFilename.isNullOrEmpty()) {
            val front = uploadService.createUpload(memberId, image2, request)
            guideTemp.frontImgUrl = front.url
        }
        if(!image3?.originalFilename.isNullOrEmpty()) {
            val back = uploadService.createUpload(memberId, image3, request)
            guideTemp.backImgUrl = back.url
        }
        if(!image4?.originalFilename.isNullOrEmpty()) {
            val left = uploadService.createUpload(memberId, image4, request)
            guideTemp.leftImgUrl = left.url
        }
        if(!image5?.originalFilename.isNullOrEmpty()) {
            val right = uploadService.createUpload(memberId, image5, request)
            guideTemp.rightImgUrl = right.url
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createGuideAdditionalCarImages(
        image1: MultipartFile?,
        image2: MultipartFile?,
        image3: MultipartFile?,
        image4: MultipartFile?,
        image5: MultipartFile?,
        guideId: Long,
        memberId: Long,
        request: HttpServletRequest
    ): Unit {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found temp guide") }

        val addImages = mutableListOf<GuideAddImg>()
        if(image1 != null && !image1.isEmpty) {
            val uploadDTO = uploadService.createUpload(memberId, image1, request)
            addImages.add(
                GuideAddImg(
                    guide = guide,
                    imgUrl = uploadDTO.url,
                    imgType = ImgType.ADDITIONAL
                )
            )
        }

        if(image2 != null && !image2.isEmpty) {
            val uploadDTO = uploadService.createUpload(memberId, image2, request)
            addImages.add(
                GuideAddImg(
                    guide = guide,
                    imgUrl = uploadDTO.url,
                    imgType = ImgType.ADDITIONAL
                )
            )
        }

        if(image3 != null && !image3.isEmpty) {
            val uploadDTO = uploadService.createUpload(memberId, image3, request)
            addImages.add(
                GuideAddImg(
                    guide = guide,
                    imgUrl = uploadDTO.url,
                    imgType = ImgType.ADDITIONAL
                )
            )
        }

        if(image4 != null && !image4.isEmpty) {
            val uploadDTO = uploadService.createUpload(memberId, image4, request)
            addImages.add(
                GuideAddImg(
                    guide = guide,
                    imgUrl = uploadDTO.url,
                    imgType = ImgType.ADDITIONAL
                )
            )
        }

        if(image5 != null && !image5.isEmpty) {
            val uploadDTO = uploadService.createUpload(memberId, image5, request)
            addImages.add(
                GuideAddImg(
                    guide = guide,
                    imgUrl = uploadDTO.url,
                    imgType = ImgType.ADDITIONAL
                )
            )
        }
        if(!addImages.isNullOrEmpty()) {
            guideAddImgRepository.saveAll(addImages)
        }
    }
    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteAllAdditionalImages(guideId: Long, memberId: Long) {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        if(guide.memberId != memberId) {
            throw CommException("no permission")
        }
        guideAddImgRepository.deleteAllByGuideAndImgType(guide, ImgType.ADDITIONAL)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun creteGuideInspectImage(
        type: String,
        bynaryData: String,
        guideId: Long,
        memberId: Long,
        request: HttpServletRequest
    ): UploadDTO {


        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found temp guide") }
        var imageData = bynaryData.replace("data:image/png;base64,", "")
        //val decode = Base64.getDecoder().decode(imageData)
        val decode = org.apache.commons.codec.binary.Base64.decodeBase64(imageData)

        val uploadDTO = uploadService.createUpload(memberId, decode, request)

        when (type) {
            "TOP" -> {
                guide.inspectTopImgUrl = uploadDTO.url
            }
            "SIDE" -> {
                guide.inspectSideImgUrl = uploadDTO.url
            }
            "BOTTOM" -> {
                guide.inspectBottomImgUrl = uploadDTO.url
            }
        }

        uploadDTO.imgUrl = uploadDTO.url
        guideRepository.save(guide)
        return uploadDTO
    }


}
