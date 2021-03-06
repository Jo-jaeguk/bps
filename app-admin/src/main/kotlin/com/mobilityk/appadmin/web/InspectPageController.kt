package com.mobilityk.appadmin.web

import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.service.CarColorService
import com.mobilityk.core.service.DamageCheckService
import com.mobilityk.core.service.GuideCheckListService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class InspectPageController(
    private val guideService: GuideService,
    private val carColorService: CarColorService,
    private val damageCheckService: DamageCheckService,
    private val guideCheckListService: GuideCheckListService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/inspect")
    fun getInspects(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = GuideSearchOption(
            search = search,
            inspectedYn = EnumYn.Y
        )
        val page = guideService.findAllBySearchOption(searchOption, pageable)

        model["link"] = "inspect"
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        model["search"] = search ?: ""
        model["carColors"] = carColorService.findAll()
        return ModelAndView("inspect/inspect", model)
    }

    @GetMapping("/inspect/excel/download")
    fun getExcel(
        @RequestParam("search", required = false) search: String?,
        @RequestParam("check", required = false) checkList: List<String>?,
        model: MutableMap<String , Any>,
        response: HttpServletResponse
    ): Unit {
        val wb: Workbook = XSSFWorkbook()
        val sheet: Sheet = wb.createSheet("????????? ??????")
        var row: Row?
        var cell: Cell?
        var rowNum = 0
        val fileName = CommonUtil.getExcelFileName("inspect_")

        val searchOption = GuideSearchOption(
            search = search,
            inspectedYn = EnumYn.Y
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
        var size = 500
        var pageable = PageRequest.of(page, size)
        do {

            val guidePage = guideService.findAllBySearchExcel(searchOption, pageable)
            val guideContent = guidePage.content
            guideContent.forEach { guideDTO ->
                //val damageLsit = damageCheckService.findAllByGuideId(guideDTO.id!!)
                val giGyoHwanPrice = 0L
                val giPangumPrice = 0L
                val giDosaekPrice = 0L
                val needGyoHwanPrice = 0L
                val needPangumPrice = 0L
                val needDosaekPrice = 0L
                val otherPrice = 0L
                val damageTotalPrice = 0L

                val guideCheckList = guideCheckListService.findAllByGuideId(guideDTO.id!!)
                val checkListBuffer = StringBuffer()
                var checkListTotalPrice = 0L
                guideCheckList.forEach { guideCheckListDTO ->
                    checkListBuffer.append(guideCheckListDTO.question).append("/")
                    checkListTotalPrice += guideCheckListDTO.price!!
                }

                val options = guideService.getGuideCarOptions(guideDTO.id!!)
                val optionBuffer = StringBuffer()
                options.forEach { guideOptionDTO ->
                    optionBuffer.append(guideOptionDTO.option).append("/")
                }

                row = sheet.createRow(rowNum++)
                for (i in 0..index) {
                    var check = map[i]
                    cell = row?.createCell(i)
                    when(check) {
                        "excel_serial" -> cell?.setCellValue(guideDTO.serial.toString())
                        "excel_member_name" -> cell?.setCellValue(guideDTO.member?.name)
                        "excel_member_emailAddress" -> cell?.setCellValue(guideDTO.member?.emailAddress)
                        "excel_member_phone" -> cell?.setCellValue(guideDTO.member?.phone)
                        "excel_member_manage_branch" -> cell?.setCellValue(guideDTO.member?.manageBranch)
                        "excel_car_manufacturer" -> cell?.setCellValue(guideDTO.carManufacturer)
                        "excel_car_model" -> cell?.setCellValue(guideDTO.carModel)
                        "excel_car_model_detail" -> cell?.setCellValue(guideDTO.carModelDetail)
                        "excel_car_class" -> cell?.setCellValue(guideDTO.carClassName)
                        "excel_car_trim" -> cell?.setCellValue(guideDTO.carTrim)
                        "excel_car_maded_year" -> cell?.setCellValue(guideDTO.carMadedYear.toString() + "/" + guideDTO.carMadedMonth.toString())
                        "excel_fuel_type" -> cell?.setCellValue(guideDTO.fuelType?.description)
                        "excel_car_displacement" -> cell?.setCellValue(guideDTO.carDisplacement.toString())
                        "excel_motor_type" -> cell?.setCellValue(guideDTO.motorType?.description)
                        "excel_mileage" -> cell?.setCellValue(guideDTO.mileage.toString())
                        "excel_accident" -> cell?.setCellValue(guideDTO.accident?.description)
                        "excel_car_color" -> cell?.setCellValue(guideDTO.carColor)
                        "excel_car_number" -> cell?.setCellValue(guideDTO.carNumber)
                        "excel_guide_price" -> cell?.setCellValue(guideDTO.price.toString())
                        "excel_inspect_minus_money" -> cell?.setCellValue("0")
                        "excel_guide_minus_money" -> cell?.setCellValue("0")
                        "excel_guide_final_buy_price" -> cell?.setCellValue(guideDTO.finalBuyPrice.toString())
                        "excel_inspect_reason" -> cell?.setCellValue(guideDTO.evaluatorMinusReason)
                        "excel_damage_change" -> cell?.setCellValue(giGyoHwanPrice.toString())
                        "excel_damage_pangum" -> cell?.setCellValue(giPangumPrice.toString())
                        "excel_damage_dosaek" -> cell?.setCellValue(giDosaekPrice.toString())
                        "excel_damage_need_change" -> cell?.setCellValue(needGyoHwanPrice.toString())
                        "excel_damage_need_pangum" -> cell?.setCellValue(needPangumPrice.toString())
                        "excel_damage_need_dosaek" -> cell?.setCellValue(needDosaekPrice.toString())
                        "excel_damage_other" -> cell?.setCellValue(otherPrice.toString())
                        "excel_damage_total" -> cell?.setCellValue(damageTotalPrice.toString())
                        "excel_check_list" -> cell?.setCellValue(checkListBuffer.toString())
                        "excel_check_list_total" -> cell?.setCellValue(checkListTotalPrice.toString())
                        "excel_tire_wear_reason" -> cell?.setCellValue(guideDTO.tireWearReason)
                        "excel_new_car_price" -> cell?.setCellValue(guideDTO.newCarPrice.toString())
                        "excel_other" -> cell?.setCellValue(guideDTO.option1 + "/" + guideDTO.option2 + "/" + guideDTO.option3)
                        "excel_option" -> cell?.setCellValue(optionBuffer.toString())
                    }
                }
            }

        } while (!guidePage.isLast)

        // ????????? ????????? ????????? ??????
        // ????????? ????????? ????????? ??????
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
            "excel_member_name" -> rowName = "????????? ??????"
            "excel_member_emailAddress" -> rowName = "????????? ?????????"
            "excel_member_phone" -> rowName = "????????? ?????????"
            "excel_member_manage_branch" -> rowName = "????????? ??????/??????"
            "excel_car_manufacturer" -> rowName = "?????????"
            "excel_car_model" -> rowName = "??????"
            "excel_car_model_detail" -> rowName = "?????? ??????"
            "excel_car_class" -> rowName = "??????"
            "excel_car_trim" -> rowName = "??????"
            "excel_car_maded_year" -> rowName = "??????"
            "excel_fuel_type" -> rowName = "?????? ??????"
            "excel_car_displacement" -> rowName = "?????????"
            "excel_motor_type" -> rowName = "??????"
            "excel_mileage" -> rowName = "????????????"
            "excel_accident" -> rowName = "????????????"
            "excel_car_color" -> rowName = "??????"
            "excel_car_number" -> rowName = "????????????"
            "excel_option" -> rowName = "??????"
            "excel_guide_price" -> rowName = "???????????????"
            "excel_inspect_minus_money" -> rowName = "???????????????"
            "excel_guide_minus_money" -> rowName = "??????????????????"
            "excel_guide_final_buy_price" -> rowName = "??????????????????"
            "excel_inspect_reason" -> rowName = "????????????"
            "excel_damage_change" -> rowName = "?????????(??????/???)"
            "excel_damage_pangum" -> rowName = "?????????(??????/???)"
            "excel_damage_dosaek" -> rowName = "?????????(??????/???)"
            "excel_damage_need_change" -> rowName = "????????????(??????/???)"
            "excel_damage_need_pangum" -> rowName = "????????????(??????/???)"
            "excel_damage_need_dosaek" -> rowName = "????????????(??????/???)"
            "excel_damage_other" -> rowName = "??????(??????/???)"
            "excel_damage_total" -> rowName = "??????(???)"
            "excel_check_list" -> rowName = "???????????????"
            "excel_check_list_total" -> rowName = "??????????????? ??????(???)"
            "excel_tire_wear_reason" -> rowName = "??????????????? ????????????"
            "excel_new_car_price" -> rowName = "????????????"
            "excel_other" -> rowName = "????????????"
        }
        return rowName
    }
}