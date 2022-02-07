package com.mobilityk.appadmin.web

import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.MarketName
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.service.CarColorService
import com.mobilityk.core.service.GuidePriceService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.util.CommonUtil
import com.mobilityk.core.util.FileService
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ResourceLoader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.math.log

@Controller
@RequestMapping("/admin")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class GuidePageController(
    private val guideService: GuideService,
    private val carColorService: CarColorService,
    private val fileService: FileService,
    private val guidePriceService: GuidePriceService,
    private val resourceLoader: ResourceLoader
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/guide")
    fun getGuides(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = GuideSearchOption(
            search = search
        )
        val page = guideService.findAllBySearchOption(searchOption, pageable)

        model["link"] = "guide"
        page.content.forEach { it.convertData() }
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        model["search"] = search ?: ""
        model["carColors"] = carColorService.findAll()

        return ModelAndView("guide/guide", model)
    }

    @GetMapping("/guide/detail/{guideId}")
    fun getGuideDetail(
        @PathVariable("guideId") guideId: Long,
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["guideId"] = guideId

        val searchOption = GuideSearchOption(
            search = search
        )
        val page = guideService.findAllBySearchOption(searchOption, pageable)

        model["link"] = "guide"
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        model["search"] = search ?: ""

        return ModelAndView("guide/guide", model)
    }

    @GetMapping("/guide/excel/download")
    fun getExcel(
        @RequestParam("search", required = false) search: String?,
        @RequestParam("check", required = false) checkList: List<String>?,
        model: MutableMap<String , Any>,
        response: HttpServletResponse
    ): Unit {
        val wb: Workbook = XSSFWorkbook()
        val sheet: Sheet = wb.createSheet("첫번째 시트")
        var row: Row?
        var cell: Cell?
        var rowNum = 0
        val fileName = CommonUtil.getExcelFileName("guide_")

        // Body
        val searchOption = GuideSearchOption(
            search = search
        )

        var index = 0
        var map = mutableMapOf<Int, String>()
        row = sheet.createRow(rowNum++)

        checkList?.forEach { check ->
            cell = row?.createCell(index)
            cell?.setCellValue(getRowName(check))
            map[index] = check
            index++
        }

        var page = 0
        var size = 1000
        var pageable = PageRequest.of(page, size)
        do {
            val guidePage = guideService.findAllBySearchExcel(searchOption, pageable)
            logger.info { "get excel page ${pageable.pageNumber} size ${pageable.pageSize} content len ${guidePage.totalElements}" }
            val guideContent = guidePage.content

            try {
                guideContent.forEach { guideDTO ->
                    /*
                    val guidePriceList = guidePriceService.findAllByGuideId(guideDTO.id!!)
                    var guideEncarPrice = 0L
                    var guideKcarPrice = 0L
                    var guideKBPrice = 0L
                    var guideHowPrice = 0L
                    var guideKcarBid = 0L
                    var guideCarAuctionBid = 0L
                    guidePriceList.forEach { guidePriceDTO ->
                        if(guidePriceDTO.marketType == MarketType.RETAIL) {
                            when(guidePriceDTO.marketNameCode) {
                                MarketName.RETAIL_SK_ENCAR -> guideEncarPrice = guidePriceDTO.price!!
                                MarketName.RETAIL_KCAR -> guideKcarPrice = guidePriceDTO.price!!
                                MarketName.RETAIL_KB -> guideKBPrice = guidePriceDTO.price!!
                                MarketName.RETAIL_HOW -> guideHowPrice = guidePriceDTO.price!!
                            }
                        } else {
                            when(guidePriceDTO.marketNameCode) {
                                MarketName.BID_CAR_AUCTION -> guideCarAuctionBid = guidePriceDTO.price!!
                                MarketName.BID_KCAR -> guideKcarBid = guidePriceDTO.price!!
                            }
                        }
                    }
                     */
                    /*
                    logger.info { "guideDTO.guideEncarPrice ${guideDTO.guideEncarPrice}" }
                    logger.info { "guideDTO.guideKcarPrice ${guideDTO.guideKcarPrice}" }
                    logger.info { "guideDTO.guideKBPrice ${guideDTO.guideKBPrice}" }
                    logger.info { "guideDTO.guideHowPrice ${guideDTO.guideHowPrice}" }
                    logger.info { "guideDTO.guideKcarBid ${guideDTO.guideKcarBid}" }
                    logger.info { "guideDTO.guideCarAuctionBid ${guideDTO.guideCarAuctionBid}" }
                     */
                    var guideEncarPrice = if(guideDTO.guideEncarPrice == null) 0L else guideDTO.guideEncarPrice
                    var guideKcarPrice = if(guideDTO.guideKcarPrice == null) 0L else guideDTO.guideKcarPrice
                    var guideKBPrice = if(guideDTO.guideKBPrice == null) 0L else guideDTO.guideKBPrice
                    var guideHowPrice = if(guideDTO.guideHowPrice == null) 0L else guideDTO.guideHowPrice
                    var guideKcarBid = if(guideDTO.guideKcarBid == null) 0L else guideDTO.guideKcarBid
                    var guideCarAuctionBid = if(guideDTO.guideCarAuctionBid == null) 0L else guideDTO.guideCarAuctionBid

                    row = sheet.createRow(rowNum++)
                    for (i in 0..index) {
                        var check = map[i]
                        cell = row?.createCell(i)
                        when(check) {
                            "excel_serial" -> cell?.setCellValue(guideDTO.serial)
                            "excel_manage_name" -> cell?.setCellValue(guideDTO.adminName)
                            "excel_manage_emailAddress" -> cell?.setCellValue(guideDTO.customerManager?.name)
                            "excel_manage_phone" -> cell?.setCellValue(guideDTO.customerManager?.phone)
                            "excel_manage_branch" -> cell?.setCellValue(guideDTO.customerManager?.branch)
                            "excel_customer_name" -> cell?.setCellValue(guideDTO.customerName)
                            "excel_customer_phone" -> cell?.setCellValue(guideDTO.customerContact)
                            "excel_guide_encar" -> cell?.setCellValue(guideEncarPrice.toString())
                            "excel_guide_kcar" -> cell?.setCellValue(guideKcarPrice.toString())
                            "excel_guide_kb" -> cell?.setCellValue(guideKBPrice.toString())
                            "excel_guide_how" -> cell?.setCellValue(guideHowPrice.toString())
                            "excel_guide_avg" -> cell?.setCellValue(guideDTO.retailAvgPrice.toString())
                            "excel_guide_bid_kcar" -> cell?.setCellValue(guideKcarBid.toString())
                            "excel_guide_bid_auction" -> cell?.setCellValue(guideCarAuctionBid.toString())
                            "excel_guide_bid_avg" -> cell?.setCellValue(guideDTO.bidAvgPrice.toString())
                            "excel_guide_price" -> cell?.setCellValue(guideDTO.price.toString())
                            "excel_guide_send_price" -> cell?.setCellValue(guideDTO.finalBuyPrice.toString())
                            "excel_car_manufacturer" -> cell?.setCellValue(guideDTO.carManufacturer.toString())
                            "excel_car_model" -> cell?.setCellValue(guideDTO.carModel.toString())
                            "excel_car_model_detail" -> cell?.setCellValue(guideDTO.carModelDetail.toString())
                            "excel_car_class" -> cell?.setCellValue(guideDTO.carClassName.toString())
                            "excel_car_trim" -> cell?.setCellValue(guideDTO.carTrim.toString())
                            "excel_car_maded_year" -> cell?.setCellValue(guideDTO.carMadedYear.toString() + "/" + guideDTO.carMadedMonth.toString())
                            "excel_fuel_type" -> cell?.setCellValue(guideDTO.fuelType?.description)
                            "excel_car_displacement" -> cell?.setCellValue(guideDTO.carDisplacement.toString())
                            "excel_motor_type" -> cell?.setCellValue(guideDTO.motorType?.description)
                            "excel_mileage" -> cell?.setCellValue(guideDTO.mileage.toString())
                            "excel_accident" -> cell?.setCellValue(guideDTO.accident?.description)
                            "excel_car_color" -> cell?.setCellValue(guideDTO.carColor)
                            "excel_car_number" -> cell?.setCellValue(guideDTO.carNumber)
                            "excel_option" -> cell?.setCellValue(guideDTO.option1 + "/" + guideDTO.option2 + "/" + guideDTO.option3)
                            "excel_admin_name" ->  cell?.setCellValue(guideDTO.adminName)
                            "excel_admin_phone" -> cell?.setCellValue(guideDTO.adminPhone)
                            "excel_guide_status" -> cell?.setCellValue(guideDTO.guideStatus?.description)
                            "excel_car_location" -> cell?.setCellValue(guideDTO.carLocation)
                            "excel_new_car_price" -> cell?.setCellValue(guideDTO.newCarPrice.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                logger.info { "Exception" }
                logger.info { "${CommonUtil.getExceptionPrintStack(e)}" }
            }
            logger.info { "guidePage has Next ${guidePage.hasNext()}" }
            pageable = pageable.next()

        } while (!guidePage.isLast)

        logger.info { "while break" }
        // Header
        // 컨텐츠 타입과 파일명 지정
        response.contentType = "ms-vnd/excel"
        response.setHeader("Content-Disposition", "attachment;filename=$fileName.xlsx")
        // Excel File Output
        // Excel File Output
        wb.write(response.outputStream)
        wb.close()
    }

    private fun getRowName(check: String): String {
        var rowName: String = ""
        when(check) {
            "excel_serial" -> rowName = "QN#"
            "excel_manage_name" -> rowName = "고객담당자 이름"
            "excel_manage_emailAddress" -> rowName = "고객담당자 아이디"
            "excel_manage_phone" -> rowName = "고객담당자 연락처"
            "excel_manage_branch" -> rowName = "고객담당자 소속/지점"
            "excel_customer_name" -> rowName = "고객정보 고객명"
            "excel_customer_phone" -> rowName = "고객정보 고객연락처"
            "excel_guide_encar" -> rowName = "가이드 엔카"
            "excel_guide_kcar" -> rowName = "가이드 Kcar"
            "excel_guide_kb" -> rowName = "가이드 kb차차차"
            "excel_guide_how" -> rowName = "가이드 카매니저"
            "excel_guide_avg" -> rowName = "가이드 평균"
            "excel_guide_bid_kcar" -> rowName = "Kcar 경매장"
            "excel_guide_bid_auction" -> rowName = "Car Auction 경매장"
            "excel_guide_bid_avg" -> rowName = "경매장가 평균"
            "excel_guide_price" -> rowName = "가이드 가격"
            "excel_guide_send_price" -> rowName = "가이드 전송가격"
            "excel_car_manufacturer" -> rowName = "브랜드"
            "excel_car_model" -> rowName = "모델"
            "excel_car_model_detail" -> rowName = "상세모델"
            "excel_car_class" -> rowName = "등급"
            "excel_car_trim" -> rowName = "트림"
            "excel_car_maded_year" -> rowName = "년식"
            "excel_fuel_type" -> rowName = "연료 방식"
            "excel_car_displacement" -> rowName = "배기량"
            "excel_motor_type" -> rowName = "미션"
            "excel_mileage" -> rowName = "주행거리"
            "excel_accident" -> rowName = "사고유무"
            "excel_car_color" -> rowName = "색상"
            "excel_car_number" -> rowName = "차량번호"
            "excel_option" -> rowName = "옵션"
            "excel_admin_name" -> rowName = "제출자 이름"
            "excel_admin_phone" -> rowName = "제출자 연락처"
            "excel_guide_status" -> rowName = "가이드 상태"
            "excel_car_location" -> rowName = "차량 위치"
            "excel_new_car_price" -> rowName = "신차가격"

        }
        return rowName
    }

    @GetMapping("/guide/download/image")
    fun getImages(
        @RequestParam("guideId", required = true) guideId: Long,
        model: MutableMap<String , Any>,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Unit {

        val guideImages = guideService.getGuideImages(guideId)
        var imageList = mutableListOf<File>()
        guideImages.forEach { guideAddImgDTO ->

            val url = URL(guideAddImgDTO.imgUrl)
            var path = resourceLoader.getResource("classpath:").uri.path + "/image/"

            //path = "/Users/yunjisang/Desktop/DEV/image/"
            val now = LocalDateTime.now()
            val todayDate: String = CommonUtil.getTodayDate(now)
            val filePath = todayDate + "/" + guideAddImgDTO.id.toString() + "/"
            val fileName: String = guideAddImgDTO.id.toString() + "_" + now.second + "_" + now.nano + ".png"
            path += filePath

            var storeFile = File(path)
            if (!storeFile.exists()) {
                storeFile.mkdirs()
            }

            val targetFile = Paths.get(path, fileName)
            try {
                val inputStream = url.openStream()
                Files.copy(inputStream, targetFile)
                //FileCopyUtils.copy(buffer!!, targetFile.toFile())
                imageList.add(File(path + fileName))
                inputStream.close()
            } catch (e: Exception) {
                logger.error { "exception" }
                logger.error { CommonUtil.getExceptionPrintStack(e) }
            } finally {

            }

        }
        fileService.compressFiles(
            imageList,
            guideId.toString(),
            request, response
        )
    }


}