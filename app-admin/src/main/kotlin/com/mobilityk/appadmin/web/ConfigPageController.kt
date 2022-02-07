package com.mobilityk.appadmin.web

import com.mobilityk.core.domain.CarClassSearchOption
import com.mobilityk.core.domain.CarManufacturerSearchOption
import com.mobilityk.core.domain.CarTrimSearchOption
import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.DamageCheckSearchOption
import com.mobilityk.core.domain.GuideNotificationSearch
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.domain.PriceConfigType
import com.mobilityk.core.service.CarClassService
import com.mobilityk.core.service.CarColorService
import com.mobilityk.core.service.CarManufacturerService
import com.mobilityk.core.service.CarModelService
import com.mobilityk.core.service.CarTrimService
import com.mobilityk.core.service.CheckListConfigService
import com.mobilityk.core.service.DamageCheckService
import com.mobilityk.core.service.GuideNotificationService
import com.mobilityk.core.service.GuidePriceConfigCommentService
import com.mobilityk.core.service.GuidePriceConfigService
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
data class ConfigPageController(
    private val carManufacturerService: CarManufacturerService,
    private val carModelService: CarModelService,
    private val carColorService: CarColorService,
    private val carClassService: CarClassService,
    private val carTrimService: CarTrimService,
    private val guidePriceConfigService: GuidePriceConfigService,
    private val guidePriceConfigCommentService: GuidePriceConfigCommentService,
    private val checkListService: CheckListConfigService,
    private val damageCheckService: DamageCheckService,
    private val guideNotificationService: GuideNotificationService
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/config/car")
    fun carConfig(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = CarTrimSearchOption(
            search = if(search.isNullOrEmpty()) null else search
        )
        val page = carTrimService.findAllBySearchOption(searchOption, pageable)
        model["link_parent"] = "config"
        model["link"] = "car_config"
        model["content"] = page.content
        model["carManufacturerList"] = carManufacturerService.findAll()
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("config/car_config_v2" , model)
        //return ModelAndView("config/car_manufacturer_config" , model)
    }

    @GetMapping("/config/car_manufacturer")
    fun carManufacturer(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = CarManufacturerSearchOption(
            name = if(search.isNullOrEmpty()) null else search
        )
        val page = carManufacturerService.findAllBySearchOption(searchOption = searchOption, pageable = pageable)
        model["link_parent"] = "config"
        model["link"] = "car_manufacturer"
        model["content"] = page.content
        model["carManufacturerList"] = carManufacturerService.findAll()
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("config/car_manufacturer_config" , model)
    }


    @GetMapping("/config/checklist")
    fun inspect(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["orderIndex"], direction = Sort.Direction.ASC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {

        val page = if(search.isNullOrEmpty()) {
            checkListService.findAll(pageable)
        } else {
            checkListService.findAllBySearch(search, pageable)
        }
        model["base_price"] = checkListService.getCheckListBasePrice()
        model["link_parent"] = "config"
        model["link"] = "checklist_config"
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("config/checklist_config" , model)
    }

    @GetMapping("/config/damage")
    fun damage(
        @RequestParam("search", required = false) search: String?,
        @RequestParam("carType", required = false) carType: CarTypeEnum?,
        @PageableDefault(page = 0, size = 300, sort = ["createdAt"], direction = Sort.Direction.ASC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {

        val searchOption = DamageCheckSearchOption(
            search = search,
            carType = carType ?: CarTypeEnum.SMALL
        )
        val page = damageCheckService.findAllBySearch(searchOption, pageable)

        model["car_type"] = carType ?: CarTypeEnum.SMALL
        model["link_parent"] = "config"
        model["link"] = "damage_config"
        page.content.forEach {
            it.convertToStr()
        }
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("config/damage_config" , model)
    }

    @GetMapping("/config/guide_notification")
    fun notification(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = GuideNotificationSearch(
            search = search
        )
        val page = guideNotificationService.findAllBySearch(searchOption, pageable)
        model["link_parent"] = "config"
        model["link"] = "guide_notification"
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination

        model["car_manufacturer_list"] = carManufacturerService.findAll()

        return ModelAndView("config/guide_notification_config" , model)
    }

    @GetMapping("/config/car/excel/download")
    fun carConfigExcel(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>,
        response: HttpServletResponse
    ): Unit {
        val searchOption = CarClassSearchOption(
            search = if(search.isNullOrEmpty()) null else search
        )
        val page = carClassService.findAllBySearchOption(searchOption, pageable)
        val wb: Workbook = XSSFWorkbook()
        val sheet: Sheet = wb.createSheet("sheet1")
        var row: Row?
        var cell: Cell?
        var rowNum = 0
        val fileName = CommonUtil.getExcelFileName("car_config_")

        // Header
        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("번호")
        cell = row.createCell(1)
        cell.setCellValue("브랜드")
        cell = row.createCell(2)
        cell.setCellValue("모델")
        cell = row.createCell(3)
        cell.setCellValue("세부")
        cell = row.createCell(4)
        cell.setCellValue("등급")
        cell = row.createCell(5)
        cell.setCellValue("인기")

        // Body
        page.content.forEach { configDTO ->
            row = sheet.createRow(rowNum++)
            cell = row?.createCell(0)
            cell?.setCellValue(configDTO.id.toString())
            cell = row?.createCell(1)
            cell?.setCellValue(configDTO.carManufacturerName)
            cell = row?.createCell(2)
            cell?.setCellValue(configDTO.carModelName)
            cell = row?.createCell(3)
            cell?.setCellValue(configDTO.carModelNameDetail)
            cell = row?.createCell(4)
            cell?.setCellValue(configDTO.name)
            cell = row?.createCell(5)
            cell?.setCellValue(configDTO.popularType?.description)
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


    @GetMapping("/config/check_list/excel/download")
    fun checkListExcel(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>,
        response: HttpServletResponse
    ): Unit {
        val checkList = checkListService.findAllBySearchExcel()
        val wb: Workbook = XSSFWorkbook()
        val sheet: Sheet = wb.createSheet("sheet1")
        var row: Row?
        var cell: Cell?
        var rowNum = 0
        val fileName = CommonUtil.getExcelFileName("check_list_")

        // Header
        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("순서")
        cell = row.createCell(1)
        cell.setCellValue("문항내용")
        cell = row.createCell(2)
        cell.setCellValue("금액")
        cell = row.createCell(3)
        cell.setCellValue("작성자(아이디)")
        cell = row.createCell(4)
        cell.setCellValue("숨김여부")

        // Body
        checkList.forEach { configDTO ->
            row = sheet.createRow(rowNum++)
            cell = row?.createCell(0)
            cell?.setCellValue(configDTO.orderIndex.toString())
            cell = row?.createCell(1)
            cell?.setCellValue(configDTO.question)
            cell = row?.createCell(2)
            cell?.setCellValue(configDTO.price.toString())
            cell = row?.createCell(3)
            cell?.setCellValue(configDTO.writerLoginId)
            cell = row?.createCell(4)
            cell?.setCellValue(if(configDTO.published == true) "활성화" else "숨김")
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

    private fun getPricecConfig(value: String?): String {
        return if(value.isNullOrEmpty()) {
            "0"
        } else {
            value
        }
    }

    @GetMapping("/config/guide")
    fun guideConfig(
        model: MutableMap<String, Any>
    ): ModelAndView {

        val guidePriceConfigList = guidePriceConfigService.findAll()

        guidePriceConfigList.forEach { priceConfig ->
            if(priceConfig.priceConfigType == PriceConfigType.RETAIL_CONST) {
                model["retail_const"] = priceConfig.value!!
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVERAGE &&
                priceConfig.countryType == CountryType.DOMESTIC) {
                model["domestic_avg"] = getPricecConfig(priceConfig.value)
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVERAGE &&
                priceConfig.countryType == CountryType.INTERNATIONAL) {
                model["international_avg"] = getPricecConfig(priceConfig.value)
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_100_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_100_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_100_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_100_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_100_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_100_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_200_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_200_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_200_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_200_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_200_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_200_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_300_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_300_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_300_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_300_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_300_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_300_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_400_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_400_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_400_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_400_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_400_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_400_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_500_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_500_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_500_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_500_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_500_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_500_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_600_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_600_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_600_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_600_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_600_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_600_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_700_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_700_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_700_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_700_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_700_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_700_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_800_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_800_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_800_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_800_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_800_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_800_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_900_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_900_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_900_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_900_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_900_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_900_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1000_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1000_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1000_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1000_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1000_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1000_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1100_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1100_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1100_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1100_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1100_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1100_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1200_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1200_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1200_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1200_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1200_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1200_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1300_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1300_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1300_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1300_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1300_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1300_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1400_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1400_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1400_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1400_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1400_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1400_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1500_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1500_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1500_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1500_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1500_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1500_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1600_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1600_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1600_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1600_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1600_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1600_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1700_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1700_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1700_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1700_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1700_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1700_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1800_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1800_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1800_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1800_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1800_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1800_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_1900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1900_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1900_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_1900_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_1900_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_1900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_1900_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_1900_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2000_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2000_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2000_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2000_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2000_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2000_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2100_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2100_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2100_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2100_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2100_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2100_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2200_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2200_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2200_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2200_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2200_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2200_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2300_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2300_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2300_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2300_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2300_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2300_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2400_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2400_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2400_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2400_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2400_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2400_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2500_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2500_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2500_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2500_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2500_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2500_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2600_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2600_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2600_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2600_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2600_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2600_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2700_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2700_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2700_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2700_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2700_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2700_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2800_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2800_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2800_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2800_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2800_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2800_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_2900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2900_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2900_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_2900_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_2900_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_2900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_2900_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_2900_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3000_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3000_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3000_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3000_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3000_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3000_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3100_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3100_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3100_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3100_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3100) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3100_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3100_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3200_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3200_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3200_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3200_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3200) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3200_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3200_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3300_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3300_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3300_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3300_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3300) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3300_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3300_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }

            if(priceConfig.priceConfigType == PriceConfigType.T_3400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3400_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3400_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3400_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3400_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3400) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3400_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3400_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3500_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3500_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3500_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3500_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3500) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3500_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3500_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3600_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3600_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3600_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3600_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3600) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3600_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3600_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3700_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3700_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3700_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3700_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3700) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3700_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3700_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3800_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3800_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3800_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3800_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3800) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3800_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3800_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_3900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3900_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3900_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_3900_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_3900_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_3900) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_3900_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_3900_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_4000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_4000_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_4000_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_4000_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_4000_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_4000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_4000_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_4000_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.T_MORE_4000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_more_4000_d_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_more_4000_d_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    if(priceConfig.popularType == PopularType.POPULAR) {
                        model["t_more_4000_i_popular"] = getPricecConfig(priceConfig.value)
                    } else {
                        model["t_more_4000_i_un_popular"] = getPricecConfig(priceConfig.value)
                    }
                }
            }
            if(priceConfig.priceConfigType == PriceConfigType.AVG_T_MORE_4000) {
                if(priceConfig.countryType == CountryType.DOMESTIC) {
                    model["t_more_4000_d_avg"] = getPricecConfig(priceConfig.value)
                } else if(priceConfig.countryType == CountryType.INTERNATIONAL) {
                    model["t_more_4000_i_avg"] = getPricecConfig(priceConfig.value)
                }
            }

        }

        model["comments"] = guidePriceConfigCommentService.findAllWithMember()
        model["link_parent"] = "config"
        model["link"] = "guide_config"
        return ModelAndView("config/guide_config" , model)
    }

}