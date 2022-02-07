package com.mobilityk.appadmin.web

import com.mobilityk.core.domain.LogType
import com.mobilityk.core.repository.search.ActivityLogSearchOption
import com.mobilityk.core.repository.search.MemberSearchOption
import com.mobilityk.core.service.ActivityLogService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.data.domain.Page
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class ActivityLogPageController(
    private val activityLogService: ActivityLogService
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/log/member")
    fun getLogs(
        @RequestParam("search", required = false) search: String?,
        @RequestParam("startedAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") startedAt: LocalDateTime?,
        @RequestParam("endedAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") endedAt: LocalDateTime?,
        @PageableDefault(page = 0, size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = ActivityLogSearchOption(
            search = if(search.isNullOrEmpty()) null else search,
            logType = LogType.MEMBER_LOG,
            startedAt = startedAt,
            endedAt = endedAt
        )
        val page = activityLogService.findAllBySearchOption(searchOption, pageable)
        model["link_parent"] = "log"
        model["link"] = "member_log"
        page.content.forEach { it.replaceNewLine() }
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("log/member_log" , model)
    }

    @GetMapping("/log/guide")
    fun guideLog(
        @RequestParam("search", required = false) search: String?,
        @RequestParam("startedAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") startedAt: LocalDateTime?,
        @RequestParam("endedAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") endedAt: LocalDateTime?,
        @PageableDefault(page = 0, size = 5, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = ActivityLogSearchOption(
            search = if(search.isNullOrEmpty()) null else search,
            logType = LogType.GUIDE_LOG,
            startedAt = startedAt,
            endedAt = endedAt
        )
        val page = activityLogService.findAllBySearchOption(searchOption, pageable)
        model["link_parent"] = "log"
        model["link"] = "guide_log"
        page.content.forEach { it.replaceNewLine() }
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("log/guide_log" , model)
    }

    @GetMapping("/log/config")
    fun configLog(
        @RequestParam("search", required = false) search: String?,
        @RequestParam("startedAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") startedAt: LocalDateTime?,
        @RequestParam("endedAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") endedAt: LocalDateTime?,
        @PageableDefault(page = 0, size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = ActivityLogSearchOption(
            search = if(search.isNullOrEmpty()) null else search,
            logType = LogType.GUIDE_CONFIG_LOG,
            startedAt = startedAt,
            endedAt = endedAt
        )
        val page = activityLogService.findAllBySearchOption(searchOption, pageable)
        model["link_parent"] = "log"
        model["link"] = "config_log"

        page.content.forEach { it.replaceNewLine() }
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("log/config_log" , model)
    }

}