package com.mobilityk.appadmin.web

import com.mobilityk.core.domain.DamageType
import com.mobilityk.core.domain.buyandsell.BuyAndSellSearch
import com.mobilityk.core.domain.buyandsell.ItemType
import com.mobilityk.core.dto.buyandsell.SellDTO
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.GuideCheckListRepository
import com.mobilityk.core.repository.GuideDamageCheckRepository
import com.mobilityk.core.repository.GuideRepository
import com.mobilityk.core.repository.buyandsell.BuyTypeRepository
import com.mobilityk.core.repository.buyandsell.CarLocationRepository
import com.mobilityk.core.repository.buyandsell.NewCarDealerLocationRepository
import com.mobilityk.core.repository.buyandsell.NewCarDealerNameRepository
import com.mobilityk.core.repository.buyandsell.SellerRepository
import com.mobilityk.core.service.BuyAndSellService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.lang.StringBuilder
import java.time.LocalDateTime
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class BuyAndSellPageController(
    private val buyAndSellService: BuyAndSellService,
    private val memberService: MemberService,
    private val guideService: GuideService,
    private val guideRepository: GuideRepository,
    private val guideDamageCheckRepository: GuideDamageCheckRepository,
    private val guideCheckListRepository: GuideCheckListRepository,
    private val buyTypeRepository: BuyTypeRepository,
    private val newCarDealerLocationRepository: NewCarDealerLocationRepository,
    private val newCarDealerNameRepository: NewCarDealerNameRepository,
    private val sellerRepository: SellerRepository,
    private val carLocationRepository: CarLocationRepository
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/buyandsell")
    fun home(
        @RequestParam("car_number", required = false) carNumber: String?,
        @RequestParam("started_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") buyStartedAt: LocalDateTime?,
        @RequestParam("ended_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") buyEndedAt: LocalDateTime?,
        @RequestParam("success_started_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") buySuccessStartedAt: LocalDateTime?,
        @RequestParam("success_ended_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") buySuccessEndedAt: LocalDateTime?,
        @RequestParam("car_model", required = false) carModelName: String?,
        @RequestParam("dealer_name", required = false) dealerName: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {

        model["car_number"] = carNumber ?: ""
        model["started_at"] = if(buyStartedAt == null) "" else {
            buyStartedAt.toLocalDate().toString()
        }
        model["ended_at"] = if(buyEndedAt == null) "" else {
            buyEndedAt.toLocalDate().toString()
        }
        model["success_started_at"] = if(buySuccessStartedAt == null) "" else {
            buySuccessStartedAt.toLocalDate().toString()
        }
        model["success_ended_at"] = if(buySuccessEndedAt == null) "" else {
            buySuccessEndedAt.toLocalDate().toString()
        }
        model["car_model"] = carModelName ?: ""
        model["dealer_name"] = dealerName ?: ""


        val searchOption = BuyAndSellSearch(
            carNumber = if(carNumber.isNullOrEmpty()) null else carNumber,
            buyStartedAt = buyStartedAt,
            buyEndedAt = buyEndedAt,
            carModelName = carModelName,
            dealerName = dealerName,
            buySuccessStartedAt = buySuccessStartedAt,
            buySuccessEndedAt = buySuccessEndedAt
        )
        val page = buyAndSellService.findAllBySearch(searchOption, pageable)
        model["link"] = "buyandsell"
        page.content.forEach {


            /*
            it.carPriceTb = 0L
            var buyTotalPrice = 0L
            var sellTotalPriceTb = 0L
            val buyList = buyAndSellService.getBuyList(it.id!!)
            buyList.forEach { buyDTO ->
                /**
                 * buy Item 으로 사용되는 항목만 계산에 포함시킨다.
                 */
                val buyItemDTO = buyAndSellService.findBuyItemByItemTypeAndName(buyDTO.itemType!!, buyDTO.name!!)
                if(buyItemDTO != null) {
                    sellTotalPriceTb += buyDTO.price!!
                    if(buyDTO.itemType == ItemType.BUY_BASE_COST) {
                        buyTotalPrice += buyDTO.price!!
                        if(buyDTO.name == "차량대금") {
                            it.carPriceTb = buyDTO.price
                        }
                    }
                }
            }

            it.totalBuyItemPrice = it.sellTotalPriceTb
            */
            /*
            val sellList = buyAndSellService.getSellList(it.id!!)
            if(sellList.isNullOrEmpty()) {
                it.bidSuccessPriceTb = 0L
            } else {
                val sellDTO = sellList[0]
                it.bidSuccessPriceTb = sellDTO.sellPrice!! + sellDTO.otherCommission!!
            }
             */

            //it.sellRevenue = it.bidSuccessPriceTb!! - sellTotalPriceTb
        }
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination

        val count = page.content.size
        var stockDurationTotal = 0L
        var carPriceTbTotal = 0L
        var buyTotalPriceTotal = 0L
        var sellTotalPriceTotal = 0L
        var bidSuccessPriceTbTotal = 0L
        var sellRevenueTotal = 0L

        page.content.forEach { buyAndSellDTO ->

            buyAndSellDTO.convertData()

            stockDurationTotal += if(buyAndSellDTO.stockDuration == null) {0L} else {buyAndSellDTO.stockDuration!!}
            carPriceTbTotal += if(buyAndSellDTO.carPrice == null) {0L} else {buyAndSellDTO.carPrice!!}
            buyTotalPriceTotal += if(buyAndSellDTO.buyTotalPrice == null) {0L} else {buyAndSellDTO.buyTotalPrice!!}
            sellTotalPriceTotal += if(buyAndSellDTO.sellTotalPrice == null) {0L} else {buyAndSellDTO.sellTotalPrice!!}
            bidSuccessPriceTbTotal += if(buyAndSellDTO.bidSuccessPrice == null) {0L} else {buyAndSellDTO.bidSuccessPrice!!}
            sellRevenueTotal += if(buyAndSellDTO.sellRevenue == null) {0L} else {buyAndSellDTO.sellRevenue!!}
            val buyList = buyAndSellService.getBuyList(buyAndSellDTO.id!!)
            buyAndSellDTO.sellTotalPriceTb = 0L
            buyList.forEach { buyDTO ->
                buyAndSellDTO.sellTotalPriceTb = buyAndSellDTO.sellTotalPriceTb?.plus(buyDTO.price!!)
            }
        }
        if(stockDurationTotal != 0L) {
            model["stockDuration_avg"] = stockDurationTotal / count
        } else {
            model["stockDuration_avg"] = 0
        }
        if(carPriceTbTotal != 0L) {
            model["carPriceTb_avg"] = carPriceTbTotal / count
        } else {
            model["carPriceTb_avg"] = 0
        }
        if(buyTotalPriceTotal != 0L) {
            model["buyTotalPrice_avg"] = buyTotalPriceTotal / count
        } else {
            model["buyTotalPrice_avg"] = 0
        }
        if(sellTotalPriceTotal != 0L) {
            model["sellTotalPrice_avg"] = sellTotalPriceTotal / count
        } else {
            model["sellTotalPrice_avg"] = 0
        }
        if(bidSuccessPriceTbTotal != 0L) {
            model["bidSuccessPriceTb_avg"] = bidSuccessPriceTbTotal / count
        } else {
            model["bidSuccessPriceTb_avg"] = 0
        }
        if(sellRevenueTotal != 0L) {
            model["sellRevenue_avg"] = sellRevenueTotal / count
        } else {
            model["sellRevenue_avg"] = 0
        }


        model["admins"] = memberService.getAdmins()

        model["buy_type_list"] = buyTypeRepository.findAllByOrderByName()
        model["new_car_dealer_location_list"] = newCarDealerLocationRepository.findAllByOrderByName()
        model["new_car_dealer_name_list"] = newCarDealerNameRepository.findAllByOrderByName()
        model["seller_list"] = sellerRepository.findAllByOrderByName()
        model["car_location_list"] = carLocationRepository.findAllByOrderByName()

        model["buy_base_cost_list"] = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_BASE_COST)
        model["buy_bid_price_list"] = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_BID_PRICE)
        model["buy_prod_price_list"] = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_PROD_PRICE)
        model["buy_retail_price_list"] = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_RETAIL_PRICE)

        return ModelAndView("buyandsell/buyandsell" , model)
    }


    @GetMapping("/buyandsell/excel/download")
    fun getExcel(
        @RequestParam("car_number", required = false) carNumber: String?,
        @RequestParam("started_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") buyStartedAt: LocalDateTime?,
        @RequestParam("ended_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") buyEndedAt: LocalDateTime?,
        @RequestParam("car_model", required = false) carModelName: String?,
        @RequestParam("dealer_name", required = false) dealerName: String?,
        @RequestParam("check", required = false) checkList: List<String>?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String , Any>,
        response: HttpServletResponse
    ): Unit {
        val wb: Workbook = XSSFWorkbook()
        val sheet: Sheet = wb.createSheet("첫번째 시트")
        var row: Row?
        var cell: Cell?
        var rowNum = 0
        val fileName = CommonUtil.getExcelFileName("buyandsell_")

        val searchOption = BuyAndSellSearch(
            carNumber = if(carNumber.isNullOrEmpty()) null else carNumber,
            buyStartedAt = buyStartedAt,
            buyEndedAt = buyEndedAt,
            carModelName = carModelName,
            dealerName = dealerName,
        )
        val buyAndSellList = buyAndSellService.findAllBySearchExcel(searchOption, pageable)

        var index = 0
        var map = mutableMapOf<Int, String>()
        row = sheet.createRow(rowNum++)

        checkList?.forEach { check ->
            cell = row?.createCell(index)
            cell?.setCellValue(getRowName(check))
            map[index] = check
            index++
        }

        val buyAndSellContent = buyAndSellList.content
        buyAndSellContent.forEach { buyAndSellDTO ->

            var sellTotalPrice = 0L

            val sellList = buyAndSellService.getSellList(buyAndSellId = buyAndSellDTO.id!!)
            var sellDTO: SellDTO
            if(!sellList.isNullOrEmpty()) {
                sellDTO = sellList[0]
                sellTotalPrice = sellDTO.hopePrice!! + sellDTO.sellPrice!! + sellDTO.otherCommission!!
            }

            var giGyoHwanCnt = 0L
            var giPangumCnt = 0L
            var giDosaekCnt = 0L
            var needGyoHwanCnt = 0L
            var needPangumCnt = 0L
            var needDosaekCnt = 0L
            var otherCnt = 0L

            var giGyoHwanPrice = 0L
            var giPangumPrice = 0L
            var giDosaekPrice = 0L
            var needGyoHwanPrice = 0L
            var needPangumPrice = 0L
            var needDosaekPrice = 0L
            var otherPrice = 0L
            var damageTotalPrice: Long

            val guide = guideRepository.findById(buyAndSellDTO.guideId!!).orElseThrow { CommException("not found guide") }
            val guideDamageList = guideDamageCheckRepository.findAllByGuide(guide)
            val guideCheckList = guideCheckListRepository.findAllByGuide(guide)

            guideDamageList.forEach { guideDamageCheck ->
                when(guideDamageCheck.damageType) {
                    DamageType.GI_GYOHWAN -> {
                        giGyoHwanCnt++
                        giGyoHwanPrice += guideDamageCheck.price!!
                    }
                    DamageType.GI_DOSAEK -> {
                        giDosaekCnt++
                        giDosaekPrice += guideDamageCheck.price!!
                    }
                    DamageType.GI_PANGUM -> {
                        giPangumCnt++
                        giPangumPrice += guideDamageCheck.price!!
                    }
                    DamageType.NEED_GYOHWAN -> {
                        needGyoHwanCnt++
                        needGyoHwanPrice += guideDamageCheck.price!!
                    }
                    DamageType.NEED_DOSAEK -> {
                        needDosaekCnt++
                        needDosaekPrice += guideDamageCheck.price!!
                    }
                    DamageType.NEED_PANGUM-> {
                        needPangumCnt++
                        needPangumPrice += guideDamageCheck.price!!
                    }
                    DamageType.OTHER-> {
                        otherCnt++
                        otherPrice += guideDamageCheck.price!!
                    }
                }
            }
            damageTotalPrice = giGyoHwanPrice + giDosaekPrice + giPangumPrice + needDosaekPrice + needGyoHwanPrice + needPangumPrice + otherPrice

            val buffer = StringBuffer()
            var checkListTotalPrice = 0L
            guideCheckList.forEach { innerGuideCheckList ->
                buffer.append(innerGuideCheckList.question).append("/")
                checkListTotalPrice += innerGuideCheckList.price!!
            }

            val checkMinusPrice = buyAndSellDTO.guide?.checkMinusPrice ?: 0L
            val damageMinusPrice = buyAndSellDTO.guide?.damageMinusPrice ?: 0L
            val inspectMinusPrice = checkMinusPrice + damageMinusPrice

            row = sheet.createRow(rowNum++)
            for (i in 0..index) {
                var check = map[i]
                cell = row?.createCell(i)
                when(check) {
                    "excel_buy_date" -> cell?.setCellValue(buyAndSellDTO.buyDateStr)
                    "excel_car_manufacturer" -> cell?.setCellValue(buyAndSellDTO.guide?.carManufacturer)
                    "excel_member_emailAddress" -> cell?.setCellValue(buyAndSellDTO.member?.emailAddress)
                    "excel_car_model" -> cell?.setCellValue(buyAndSellDTO.guide?.carModel)
                    "excel_car_model_detail" -> cell?.setCellValue(buyAndSellDTO.guide?.carModelDetail)
                    "excel_car_class" -> cell?.setCellValue(buyAndSellDTO.guide?.carClassName)
                    "excel_car_trim" -> cell?.setCellValue(buyAndSellDTO.guide?.carTrim)
                    "excel_car_number" -> cell?.setCellValue(buyAndSellDTO.guide?.carNumber)
                    "excel_car_color" -> cell?.setCellValue(buyAndSellDTO.guide?.carColor)
                    "excel_car_maded_year" -> cell?.setCellValue(buyAndSellDTO.guide?.carMadedYear.toString() + "/" + buyAndSellDTO.guide?.carMadedMonth)
                    "excel_mileage" -> cell?.setCellValue(buyAndSellDTO.guide?.mileage.toString())
                    "excel_modify_car_number" -> cell?.setCellValue(buyAndSellDTO.modifyCarNumber)
                    "excel_buyer" -> cell?.setCellValue(buyAndSellDTO.buyer?.emailAddress)
                    "excel_buy_type" -> cell?.setCellValue(buyAndSellDTO.buyType)
                    "excel_new_car_dealer_location" -> cell?.setCellValue(buyAndSellDTO.newCarDealerLocation)
                    "excel_new_car_dealer_name" -> cell?.setCellValue(buyAndSellDTO.newCarDealerName)
                    "excel_retail_avg" -> cell?.setCellValue(buyAndSellDTO.guide?.retailAvgPrice.toString())
                    "excel_sell_price_sum" -> cell?.setCellValue(sellTotalPrice.toString())
                    "excel_sell_expect_price" -> cell?.setCellValue(buyAndSellDTO.sellExpectPrice.toString())
                    "excel_expect_revenue" -> cell?.setCellValue(buyAndSellDTO.expectRevenue.toString())
                    "excel_sell_price" -> cell?.setCellValue(buyAndSellDTO.sellSellPrice.toString())
                    "excel_sell_revenue" -> cell?.setCellValue(buyAndSellDTO.sellRevenue.toString())
                    "excel_sell_diff_price" -> cell?.setCellValue(buyAndSellDTO.sellDiffPrice.toString())
                    "excel_sell_revenue_diff_price" -> cell?.setCellValue(buyAndSellDTO.sellRevenueDiff.toString())

                    "excel_guide_price" -> cell?.setCellValue(buyAndSellDTO.guide?.price.toString())

                    "excel_inspect_minus_money" -> cell?.setCellValue(inspectMinusPrice.toString())
                    "excel_evaluator_minus_money" -> cell?.setCellValue(buyAndSellDTO.guide?.evaluatorMinusPrice.toString())
                    "excel_guide_final_buy_price" -> cell?.setCellValue(buyAndSellDTO.guide?.finalBuyPrice.toString())

                    "excel_evaluator_reason" -> cell?.setCellValue(buyAndSellDTO.guide?.evaluatorMinusReason.toString())

                    "excel_damage_gigyohwan" -> cell?.setCellValue("$giGyoHwanCnt/$giGyoHwanPrice")
                    "excel_damage_pangum" -> cell?.setCellValue("$giPangumCnt/$giPangumPrice")
                    "excel_damage_dosaek" -> cell?.setCellValue("$giDosaekCnt/$giDosaekPrice")
                    "excel_damage_need_gigyohwan" -> cell?.setCellValue("$needGyoHwanCnt/$needGyoHwanPrice")
                    "excel_damage_need_pangum" -> cell?.setCellValue("$needPangumCnt/$needPangumPrice")
                    "excel_damage_need_dosaek" -> cell?.setCellValue("$needDosaekCnt/$needDosaekPrice")
                    "excel_damage_other" -> cell?.setCellValue("$otherCnt/$otherPrice")
                    "excel_damage_total" -> cell?.setCellValue("$damageTotalPrice")

                    "excel_check_list" -> cell?.setCellValue("${buffer.toString()}")
                    "excel_check_list_total" -> cell?.setCellValue("$checkListTotalPrice")
                }

            }

        }
        // 컨텐츠 타입과 파일명 지정
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
            "excel_buy_date" -> rowName = "매입일"
            "excel_car_manufacturer" -> rowName = "브랜드"
            "excel_member_emailAddress" -> rowName = "작성자 아이디"
            "excel_car_model" -> rowName = "모델"
            "excel_car_model_detail" -> rowName = "상세모델"
            "excel_car_class" -> rowName = "등급"
            "excel_car_trim" -> rowName = "트림"
            "excel_car_number" -> rowName = "차량번호"
            "excel_car_color" -> rowName = "색상"
            "excel_car_maded_year" -> rowName = "연식"
            "excel_mileage" -> rowName = "주행거리"
            "excel_modify_car_number" -> rowName = "수정 차량번호"
            "excel_buyer" -> rowName = "구매자"
            "excel_buy_type" -> rowName = "매입구분"
            "excel_new_car_dealer_location" -> rowName = "신차딜러(위치)"
            "excel_new_car_dealer_name" -> rowName = "신차딜러(이름)"
            "excel_retail_avg" -> rowName = "소매평균가"
            "excel_sell_price_sum" -> rowName = "매출원가 계"
            "excel_sell_expect_price" -> rowName = "판매예상가"
            "excel_expect_revenue" -> rowName = "예상이익"
            "excel_sell_price" -> rowName = "판매가"   // 판매가 + 기타수수료 수입
            "excel_sell_revenue" -> rowName = "매출이익"
            "excel_sell_diff_price" -> rowName = "판매가 차액"
            "excel_sell_revenue_diff_price" -> rowName = "매출이익 차액"

            "excel_guide_price" -> rowName = "가이드가격"
            "excel_inspect_minus_money" -> rowName = "점검감가액"
            "excel_evaluator_minus_money" -> rowName = "평가사감가액"
            "excel_guide_final_buy_price" -> rowName = "최종매입가격"

            "excel_evaluator_reason" -> rowName = "평가사유"

            "excel_damage_gigyohwan" -> rowName = "기교환(판수/원)"
            "excel_damage_pangum" -> rowName = "기판금(판수/원)"
            "excel_damage_dosaek" -> rowName = "기도색(판수/원)"
            "excel_damage_need_gigyohwan" -> rowName = "교환요망(판수/원)"
            "excel_damage_need_pangum" -> rowName = "판금요망(판수/원)"
            "excel_damage_need_dosaek" -> rowName = "도색요망(판수/원)"
            "excel_damage_other" -> rowName = "기타(판수/원)"
            "excel_damage_total" -> rowName = "합계(원)"

            "excel_check_list" -> rowName = "체크리스트항목"
            "excel_check_list_total" -> rowName = "합계(원)"

        }
        return rowName
    }


}