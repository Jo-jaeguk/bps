package com.mobilityk.appuser.web

import com.mobilityk.core.domain.Accident
import com.mobilityk.core.domain.CarModelSearchOption
import com.mobilityk.core.domain.CarTrimSearchOption
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.FuelType
import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.domain.ImgType
import com.mobilityk.core.domain.MotorType
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.service.CarClassService
import com.mobilityk.core.service.CarColorService
import com.mobilityk.core.service.CarManufacturerService
import com.mobilityk.core.service.CarModelService
import com.mobilityk.core.service.CarOptionService
import com.mobilityk.core.service.CarTrimService
import com.mobilityk.core.service.CheckListConfigService
import com.mobilityk.core.service.DamageCheckService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.GuideTempService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/user")
data class GuidePageController(
    private val guideService: GuideService,
    private val carManufacturerService: CarManufacturerService,
    private val carModelService: CarModelService,
    private val carClassService: CarClassService,
    private val carColorService: CarColorService,
    private val carTrimService: CarTrimService,
    private val carOptionService: CarOptionService,
    private val guideTempService: GuideTempService,
    private val checkListService: CheckListConfigService,
    private val damageCheckService: DamageCheckService,
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/guide")
    fun guides(
        @RequestParam("guideStatus", required = false) guideStatus: GuideStatus?,
        @RequestParam("guideStatus_ing", required = false) guideStatusIng: GuideStatus?,
        @PageableDefault(page = 0, size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>,
        @AuthenticationPrincipal user: User,
    ): ModelAndView {
        val searchOption = GuideSearchOption(
            guideStatus = if(guideStatus == GuideStatus.FINISH) null else guideStatus,
            sendYn = if(guideStatus == GuideStatus.FINISH) EnumYn.Y else null,  // Page 에서 FINISH 로 조회하는건 가이드 전송완료 상태조회
            guideStatusIng = guideStatusIng,
            memberId = user.username.toLong()
        )
        val page = guideService.findAllBySearchOption(searchOption, pageable)

        var link = if(guideStatusIng != null) {
            "ING"
        } else if(guideStatus == GuideStatus.FINISH) {
            "FINISH"
        } else if(guideStatus == GuideStatus.SELL) {
            "SELL"
        } else {
            "ALL"
        }

        model["link"] = link
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination

        return ModelAndView("guide/guide_list", model)
    }

    @GetMapping("/guide/{id}")
    fun guideDetail(
        @PathVariable("id") guideId: Long,
        model: MutableMap<String, Any>,
        @AuthenticationPrincipal user: User,
    ): ModelAndView {

        val guide = guideService.findByGuideId(guideId, user.username.toLong())
        model["item"] = guide
        model["guideId"] = guide.id.toString()

        model["guide_option_list"] = guideService.getGuideCarOptions(guide.id!!)

        /* 가이드 진행 상태에 따른 css class 명 view 전달.. */
        model["guideStatus"] = if(guide.sendYn == null || guide.sendYn != EnumYn.Y) {
            "status1"
        } else if (guide.guideStatus == GuideStatus.SELL) {
            "status2"
        } else if(guide.sendYn != null && guide.sendYn == EnumYn.Y) {
            "status3"
        } else {
            "status1"
        }
        val guidePriceList = guideService.getGuidePrices(guide.id!!)

        var unPopCnt = 0
        var popCnt = 0
        var essenCnt = 0
        val poplist = mutableListOf<PopularObj>()
        guidePriceList.forEach { guidePriceDTO ->
            when(guidePriceDTO.popularType) {
                PopularType.UN_POPULAR -> unPopCnt++
                PopularType.POPULAR ->  popCnt++
                PopularType.ESSENTIAL -> essenCnt++
            }
        }
        poplist.add(PopularObj(popularType = PopularType.UN_POPULAR, count = unPopCnt))
        poplist.add(PopularObj(popularType = PopularType.POPULAR, count = popCnt))
        poplist.add(PopularObj(popularType = PopularType.ESSENTIAL, count = essenCnt))
        poplist.sortByDescending { it.count }
        val popFirst = poplist[0]
        val popSecond = poplist[1]
        //val popThird = poplist[2]
        var reprePopularType = PopularType.POPULAR
        if(popFirst.count == popSecond.count) {
            val firstType = popFirst.popularType
            val secondType = popSecond.popularType
            if(firstType == PopularType.POPULAR) {
                if(secondType == PopularType.UN_POPULAR) {
                    reprePopularType = PopularType.POPULAR
                } else if(secondType == PopularType.ESSENTIAL) {
                    reprePopularType = PopularType.ESSENTIAL
                }
            } else if(firstType == PopularType.UN_POPULAR) {
                if(secondType == PopularType.POPULAR) {
                    reprePopularType = PopularType.POPULAR
                } else if(secondType == PopularType.ESSENTIAL) {
                    reprePopularType = PopularType.ESSENTIAL
                }
            } else if(firstType == PopularType.ESSENTIAL) {
                reprePopularType = PopularType.ESSENTIAL
            }
        }
        model["popular_type"] = reprePopularType

        return ModelAndView("guide/guide_detail", model)
    }

    @GetMapping("/guide/brand")
    fun brandStep(
        @RequestParam("countryType") countryType: CountryType,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["countryType"] = countryType
        model["content"] = carManufacturerService.findAllByCountryType(countryType)

        return ModelAndView("guide/brand", model)
    }

    @GetMapping("/guide/car_model")
    fun carModelStep(
        @RequestParam("carManufacturerId") carManufacturerId: Long,
        @RequestParam("countryType") countryType: CountryType,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = CarModelSearchOption(
            carManufacturer = carManufacturerService.findById(carManufacturerId),
            published = true
        )
        model["content"] = carModelService.findAllByCarManufacturerGroupByName(searchOption)

        val guideTempDTO = guideTempService.createCarManufacturer(user.username.toLong(), carManufacturerId, countryType)
        model["guideTempId"] = guideTempDTO.id.toString()

        return ModelAndView("guide/car_model", model)
    }

    @GetMapping("/guide/car_model_detail")
    fun carModelStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("carModelName") carModelName: String,
        model: MutableMap<String, Any>
    ): ModelAndView {

        val guideTempDTO = guideTempService.findById(guideTempId)
        val searchOption = CarModelSearchOption(
            carManufacturer = carManufacturerService.findById(guideTempDTO.carManufacturerId!!),
            carModelName = carModelName,
            published = true
        )
        model["carModelName"] = carModelName
        model["content"] = carModelService.findAllBySearchOption(searchOption)
        model["guideTempId"] = guideTempDTO.id.toString()
        return ModelAndView("guide/car_model_detail", model)
    }

    @GetMapping("/guide/car_class")
    fun carClassStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("carModelId") carModelId: Long,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        model["content"] = carClassService.findAllByCarModel(carModelId)

        guideTempService.createCarModel(user.username.toLong(), guideTempId, carModelId)

        return ModelAndView("guide/car_class", model)
    }

    @GetMapping("/guide/car_trim")
    fun carTrim(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("carClassId") carClassId: Long,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        val searchOption = CarTrimSearchOption(
            carClassId = carClassId,
            published = true
        )
        model["content"] = carTrimService.findAllBySearchOption(searchOption)

        guideTempService.createCarClass(user.username.toLong(), guideTempId, carClassId)

        return ModelAndView("guide/car_trim", model)
    }

    @GetMapping("/guide/car_made")
    fun carDisplayStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("carTrimId") carTrimId: Long,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createCarTrim(user.username.toLong(), guideTempId, carTrimId)
        return ModelAndView("guide/car_made", model)
    }

    @GetMapping("/guide/car_accident")
    fun carAccidentStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("year") year: Int,
        @RequestParam("month") month: Int,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createCarMade(user.username.toLong(), guideTempId, year, month)
        return ModelAndView("guide/car_accident", model)
    }

    @GetMapping("/guide/car_fuel")
    fun carFuelStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("accident") accident: Accident,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createAccident(user.username.toLong(), guideTempId, accident)
        return ModelAndView("guide/car_fuel", model)
    }

    @GetMapping("/guide/car_motor")
    fun carFuelStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("fuelType") fuelType: FuelType,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createFuelType(user.username.toLong(), guideTempId, fuelType)
        return ModelAndView("guide/car_motor", model)
    }

    @GetMapping("/guide/car_color")
    fun carColorStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("motorType") motorType: MotorType,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createMotorType(user.username.toLong(), guideTempId, motorType)
        model["content"] = carColorService.findAll()

        return ModelAndView("guide/car_color", model)
    }


    @GetMapping("/guide/car_mileage")
    fun carMileageStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("carColorId") carColorId: Long,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createCarColor(user.username.toLong(), guideTempId, carColorId)
        return ModelAndView("guide/car_mileage", model)
    }



    @GetMapping("/guide/car_number")
    fun carNumberStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("mileage", required = false) mileage: Int?,

        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createMileage(user.username.toLong(), guideTempId, mileage!!)
        return ModelAndView("guide/car_number", model)
    }

    @GetMapping("/guide/car_customer")
    fun carCustomerStep(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("carNumber" , required = false) carNumber: String?,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createCarNumber(user.username.toLong(), guideTempId, carNumber)
        return ModelAndView("guide/car_customer", model)
    }

    @GetMapping("/guide/car_option")
    fun carOptionStep(
        @RequestParam("step") step: Int,
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("customerName" , required = false) customerName: String?,
        @RequestParam("customerContact" , required = false) customerContact: String?,
        @RequestParam("options", required = false) options: List<String>?,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()

        if(step == 1) {
            model["options"] = carOptionService.findAll()
            guideTempService.createCustomer(user.username.toLong(), guideTempId, customerName, customerContact)
            return ModelAndView("guide/car_option_1", model)
        } else {
            guideTempService.deleteOptions(user.username.toLong(), guideTempId)
            guideTempService.createOptions(user.username.toLong(), guideTempId, options)
            return ModelAndView("guide/car_option_2", model)
        }
    }


    @GetMapping("/guide/car_image")
    fun carImage(
        @RequestParam("guideTempId") guideTempId: Long,
        @RequestParam("option1", required = false) option1: String?,
        @RequestParam("option2", required = false) option2: String?,
        @RequestParam("option3", required = false) option3: String?,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideTempId"] = guideTempId.toString()
        guideTempService.createOption(user.username.toLong(), guideTempId, option1, option2, option3)
        return ModelAndView("guide/car_image", model)
    }

    @GetMapping("/guide/{guideId}/inspect")
    fun inspect(
        @PathVariable("guideId") guideId: Long,
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideId"] = guideId
        val guideDTO = guideService.findByGuideId(guideId, user.username.toLong())
        guideDTO.convertData()
        model["guide_option_list"] = guideService.getGuideCarOptions(guideId)
        model["item"] = guideDTO

        val options = carOptionService.findAll()
        val insOptions = guideService.getGuideCarOptions(guideId)

        val checkList = checkListService.findAllByPublishedTrueOrderByOrderIndexAsc()
        val insCheckList = guideService.getGuideCheckList(guideId)


        options.forEach { carOptionDTO ->
            val insOption = insOptions.filter { it.option == carOptionDTO.option }
            if(!insOption.isNullOrEmpty()) {
                carOptionDTO.choosedYn = EnumYn.Y
            }
        }
        model["options"] = options

        checkList.forEach { checkListConfigDTO ->
            val insCheck = insCheckList.filter { it.question == checkListConfigDTO.question }
            if(!insCheck.isNullOrEmpty()) {
                checkListConfigDTO.choosedYn = EnumYn.Y
            }
        }
        model["check_list"] = checkList

        model["car_number_yn"] = if(guideDTO.carNumber.isNullOrEmpty()) "N" else "Y"
        model["ins_option_list"] = insOptions
        model["ins_checks_list"] = insOptions

        //model["ins_checks_list"] = guideDTO.inspectBottomImgUrl

        model["gauge_img_url"] = ""
        model["front_img_url"] = ""
        model["left_img_url"] = ""
        model["right_img_url"] = ""
        model["back_img_url"] = ""

        val images = guideService.getGuideImages(guideId)
        images.forEach { image ->
            when(image.imgType) {
                ImgType.GAUGE -> model["gauge_img_url"] = image.imgUrl ?: ""
                ImgType.FRONT -> model["front_img_url"] = image.imgUrl ?: ""
                ImgType.LEFT -> model["left_img_url"] = image.imgUrl ?: ""
                ImgType.RIGHT -> model["right_img_url"] = image.imgUrl ?: ""
                ImgType.BACK -> model["back_img_url"] = image.imgUrl ?: ""
            }
        }

        return ModelAndView("guide/inspect_v2", model)
    }
}

data class PopularObj (
    var popularType: PopularType? = null,
    var count: Int? = null
)