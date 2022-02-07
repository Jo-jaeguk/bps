package com.mobilityk.core.util

import com.google.gson.GsonBuilder
import com.mobilityk.core.domain.GuideOption
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.GuideOptionDTO
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.api.Pagination
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import javax.servlet.http.HttpServletRequest

object CommonUtil {
    private val logger = KotlinLogging.logger {}

    fun convertLocalDateTimeToString(dateTime: LocalDateTime): String {
        return dateTime.year.toString() + "-" + dateTime.monthValue + "-" + dateTime.dayOfMonth + " " + dateTime.hour + ":" + dateTime.minute + ":" + dateTime.second
    }

    fun getGuideKakaoAdminSendMessage(
        guideDTO: GuideDTO,
        memberDTO: MemberDTO,
        guideOptions: List<GuideOptionDTO>
    ): List<String> {
        val list = mutableListOf<String>()
        list.add(guideDTO.serial!!)
        list.add(guideDTO.customerName!!)
        list.add(guideDTO.customerContact!!)
        list.add(guideDTO.carNumber!!)
        list.add(guideDTO.carManufacturer!!)
        list.add(guideDTO.carModel!!)
        list.add(guideDTO.carModelDetail!!)
        list.add(guideDTO.carTrim!!)
        list.add(guideDTO.carClassName!!)
        list.add(guideDTO.carMadedYear.toString() + "." + guideDTO.carMadedMonth.toString())
        list.add(guideDTO.carColor!!)
        list.add(guideDTO.fuelType?.description!!)
        list.add(guideDTO.carDisplacement.toString() + "cc")
        list.add(guideDTO.motorType?.description!!)
        list.add(guideDTO.mileage.toString() + "km")
        list.add(guideDTO.accident?.description!!)
        var option: String = ""
        guideOptions.forEach { guideOptionDTO ->
            option = guideOptionDTO.option!! + " / "
        }
        list.add(option)
        return list
    }

    fun getGuideKakaoAdminBuyReqSendMessage(
        guideDTO: GuideDTO,
        memberDTO: MemberDTO,
        guideOptions: List<GuideOptionDTO>
    ): List<String> {
        val list = mutableListOf<String>()
        list.add(guideDTO.carNumber!!)
        list.add(guideDTO.serial!!)
        list.add(guideDTO.customerName!!)
        list.add(guideDTO.customerContact!!)
        list.add(guideDTO.carNumber!!)
        list.add(guideDTO.carManufacturer!!)
        list.add(guideDTO.carModel!!)
        list.add(guideDTO.carModelDetail!!)
        list.add(guideDTO.carTrim!!)
        list.add(guideDTO.carClassName!!)
        list.add(guideDTO.carMadedYear.toString() + "." + guideDTO.carMadedMonth.toString())
        list.add(guideDTO.carColor!!)
        list.add(guideDTO.fuelType?.description!!)
        list.add(guideDTO.carDisplacement.toString() + "cc")
        list.add(guideDTO.motorType?.description!!)
        list.add(guideDTO.mileage.toString() + "km")
        list.add(guideDTO.accident?.description!!)
        var option: String = ""
        guideOptions.forEach { guideOptionDTO ->
            option = guideOptionDTO.option!! + " / "
        }
        list.add(option)

        list.add(memberDTO.name!!)
        list.add(memberDTO.phone!!)
        list.add(comma(guideDTO.price!!.toInt()))
        list.add(comma(guideDTO.finalBuyPrice!!.toInt()))
        return list
    }

    fun getGuideKakaoUserSendMessage(
        guideDTO: GuideDTO,
        memberDTO: MemberDTO,
        adminDTO: MemberDTO,
        guideOptions: List<GuideOptionDTO>
    ): List<String> {
        val list = mutableListOf<String>()
        list.add(guideDTO.serial!!)
        list.add(guideDTO.customerName!!)
        list.add(guideDTO.customerContact!!)
        list.add(comma(guideDTO.sendPrice?.toInt()!!))
        list.add(adminDTO.name!!)
        list.add(adminDTO.phone!!)
        list.add(guideDTO.carNumber!!)
        list.add(guideDTO.carManufacturer!!)
        list.add(guideDTO.carModel!!)
        list.add(guideDTO.carModelDetail!!)
        list.add(guideDTO.carTrim!!)
        list.add(guideDTO.carClassName!!)
        list.add(guideDTO.carMadedYear.toString() + "." + guideDTO.carMadedMonth.toString())
        list.add(guideDTO.carColor!!)
        list.add(guideDTO.fuelType?.description!!)
        list.add(guideDTO.carDisplacement.toString() + "cc")
        list.add(guideDTO.motorType?.description!!)
        list.add(guideDTO.mileage.toString() + "km")
        list.add(guideDTO.accident?.description!!)
        var option: String = ""
        guideOptions.forEach { guideOptionDTO ->
            option = guideOptionDTO.option!! + " / "
        }
        list.add(option)
        return list
    }

    fun getGuideKakaoUserReqMessage(
        guideDTO: GuideDTO,
        guideOptions: List<GuideOptionDTO>
    ): List<String> {
        val list = mutableListOf<String>()
        list.add(guideDTO.serial!!)
        list.add(guideDTO.customerName!!)
        list.add(guideDTO.customerContact!!)
        list.add(guideDTO.carManufacturer!!)
        list.add(guideDTO.carModel!!)
        list.add(guideDTO.carModelDetail!!)
        list.add(guideDTO.carClassName!!)
        list.add(guideDTO.carMadedYear.toString() + "." + guideDTO.carMadedMonth.toString())
        list.add(guideDTO.carColor!!)
        list.add(guideDTO.fuelType?.description!!)
        list.add(guideDTO.carDisplacement.toString() + "cc")
        list.add(guideDTO.motorType?.description!!)
        list.add(guideDTO.mileage.toString() + "km")
        list.add(guideDTO.accident?.description!!)
        var option: String = ""
        guideOptions.forEach { guideOptionDTO ->
            option = guideOptionDTO.option!! + " / "
        }
        list.add(option)
        return list
    }


    fun getExceptionPrintStack(e: Exception): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    fun getExcelFileName(rootName: String): String {
        var result = rootName

        val now = LocalDateTime.now()
        result += now.year
        result += if(now.monthValue < 10) {
            "0" + now.monthValue
        } else {
            now.monthValue
        }
        result += now.dayOfMonth
        return result
    }

    fun phoneFormat(phone: String): String {
        val regEx = "(\\d{3})(\\d{3,4})(\\d{4})"

        return phone.replace(regEx, "$1-$2-$3")
    }

    fun getTodayDate(localDateTime: LocalDateTime): String{
        val year: String = localDateTime.year.toString()
        var month: String = localDateTime.monthValue.toString()
        month = if (month.length == 1) "0$month" else month
        var day: String = localDateTime.dayOfMonth.toString()
        day = if (day.length == 1) "0$day" else day
        return year + month + day
    }

    data class WeekDateTime(
        val startedAt: LocalDateTime? = null,
        val endedAt: LocalDateTime? = null,
        val index: Int? = null
    )
    fun getWeekDateTimeList(targetDateTime: LocalDateTime): List<WeekDateTime> {

        val result = mutableListOf<WeekDateTime>()
        var index = 0
        val firstDayOfMonth = LocalDateTime.of(targetDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate(), LocalTime.of(0,0,0))
        val lastDayOfMonth = LocalDateTime.of(targetDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate(), LocalTime.of(23,59,59))
        val firstSunday = LocalDateTime.of(firstDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toLocalDate() , LocalTime.of(23,59,59))
        result.add(
            WeekDateTime(
                index = index,
                startedAt = firstDayOfMonth,
                endedAt = firstSunday
            )
        )
        var startedAt = LocalDateTime.of(firstSunday.plusDays(1).toLocalDate() , LocalTime.of(0,0,0))
        var endedAt = LocalDateTime.of(startedAt.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toLocalDate() , LocalTime.of(23,59,59))
        var isLast = false
        while(true) {
            index++
            result.add(
                WeekDateTime(
                    index = index,
                    startedAt = startedAt,
                    endedAt = endedAt
                )
            )

            startedAt = LocalDateTime.of(endedAt.plusDays(1).toLocalDate(), LocalTime.of(0,0,0))
            endedAt = LocalDateTime.of(startedAt.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toLocalDate() , LocalTime.of(23,59,59))
            if(isLast) break
            if(endedAt.isAfter(lastDayOfMonth)) {
                endedAt = lastDayOfMonth
                isLast = true
            }
        }
        return result
    }

    fun comma(number: Int): String {
        val format = DecimalFormat("###,###.##")
        return format.format(number.toDouble())
    }

    fun fillZero(number: Int, length: Int): String {
        val serialStr = number.toString()
        val zeroCount = length - serialStr.length
        if(zeroCount > 0) {
            var zeroStr = ""
            for (i in 0 until zeroCount) {
                zeroStr += "0"
            }
            return zeroStr + serialStr
        } else {
            return serialStr
        }
    }

    fun fillZero(serialNumber: Int): String {
        val serialStr = serialNumber.toString()
        val zeroCount = 6 - serialStr.length

        if(zeroCount > 0) {
            var zeroStr = ""
            for (i in 0 until zeroCount) {
                zeroStr += "0"
            }
            return zeroStr + serialStr
        } else {
            return serialStr.substring(serialStr.length - 6)
        }
    }
    fun convertPage(page: Page<*>): Pagination {

        var lastPage = 0
        var endPage = 0
        var startPage = 0
        var pageRange = 10
        fun calcLastPage(totalElement: Long, size: Int) {
            lastPage = Math.ceil(totalElement.toDouble() / size.toDouble()).toInt()
        }

        fun calcStartEndPage(nowPage: Int) {
            endPage = Math.ceil(nowPage.toDouble() / pageRange.toDouble()).toInt() * pageRange

            if(lastPage < endPage) {
                endPage = lastPage
            }

            if(endPage == 0) {
                startPage = 0
            } else {
                startPage = ((endPage-1) / pageRange * 10) + 1
            }

            /*
            startPage = endPage - pageRange + 1
            if(startPage < 1) {
                startPage = 1
            }
             */
            //logger.info { "#1 start $startPage end $endPage pageRange $pageRange" }
        }

        calcLastPage(page.totalElements, page.size)
        calcStartEndPage(page.number + 1)

        return Pagination(
            isFirst = page.isFirst,
            isLast = page.isLast,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            sort = if(page.sort.isSorted) page.sort.toList()[0].property else "" ,
            direction = if(page.sort.isSorted) page.sort.toList()[0].direction.name else "",
            startPage = startPage,
            endPage = endPage,
            prevPage = page.number - 1,
            nextPage = page.number + 1
        )
    }

    fun convertZoneDateToLocalDateTime(sourceDate: String? ) : LocalDateTime {
        if(sourceDate.isNullOrEmpty()) {
            return LocalDateTime.now().minusYears(100)
        }
        val zonedDateTime = ZonedDateTime.parse(sourceDate)
        var zoneDateTimeResult = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
        return zoneDateTimeResult.toLocalDateTime()
    }

    fun convertLocalDateToZoneDateString(sourceDate: String ) : String {
        return ZonedDateTime.of(
                LocalDateTime.parse(sourceDate)
                , ZoneId.systemDefault()).toInstant().toString()
    }

    fun prettyJson(obj: Any) : String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        var result = gson.toJson(obj)
        return result
    }


    fun convertPage(page: Page<*> , paramDTO: DataTableDTO): DataTableDTO {
        var dataTableDTO: DataTableDTO = DataTableDTO()
        dataTableDTO.recordsTotal = page.totalElements.toString()
        dataTableDTO.recordsFiltered = page.totalElements.toString()
        dataTableDTO.draw = paramDTO.draw
        dataTableDTO.data = page.content
        return dataTableDTO
    }

    fun getPageable(paramDTO: DataTableDTO, request: HttpServletRequest): Pageable {
        if(isNull(request.getParameter("search[value]"))) {
            paramDTO.filter = request.getParameter("search[value]")
        }
        var orderColumn: String =  request.getParameter("columns[" + request.getParameter("order[0][column]") + "][data]")
        orderColumn = nullChange(orderColumn , "pkid")
        paramDTO.orderColumn = orderColumn
        paramDTO.orderDir = request.getParameter("order[0][dir]")

        var page: Int = when(paramDTO.start) {
            0 -> 0
            else -> {
                paramDTO.start / paramDTO.length
            }
        }
        return PageRequest.of(page , paramDTO.length ,
            when(paramDTO.orderDir?.uppercase()) {
            "DESC" -> Sort.Direction.DESC
            else -> Sort.Direction.DESC
        } , paramDTO.orderColumn)
    }

    fun isNull(str: String?) : Boolean {
        return (str == null) || str.trim().equals("") || str.trim().equals("null")
    }

    fun nullChange(str_in: String, str_out: String): String {
        var str_result = str_in
        if (str_result == "" || str_result == "null") {
            str_result = str_out
        }
        return str_result
    }
}