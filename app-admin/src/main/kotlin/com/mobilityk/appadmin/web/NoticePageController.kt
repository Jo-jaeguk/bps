package com.mobilityk.appadmin.web

import com.mobilityk.core.domain.NoticeSearchOption
import com.mobilityk.core.repository.search.MemberSearchOption
import com.mobilityk.core.service.ActivityLogService
import com.mobilityk.core.service.NoticeService
import com.mobilityk.core.util.CommonUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.data.domain.Page
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
data class NoticePageController(
    private val noticeService: NoticeService
) {

    @GetMapping("/notice")
    fun notice(
        @RequestParam("search" , required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>
    ): ModelAndView {
        val searchOption = NoticeSearchOption(
            search = search
        )
        val page = noticeService.findAllBySearch(searchOption, pageable)
        model["link"] = "notice"
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("notice/notice" , model)
    }

    @GetMapping("/notice/excel/download")
    fun noticeExcel(
        @RequestParam("search" , required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String, Any>,
        response: HttpServletResponse
    ): Unit {
        val searchOption = NoticeSearchOption(
            search = search
        )
        val page = noticeService.findAllBySearch(searchOption, pageable)
        val wb: Workbook = XSSFWorkbook()
        val sheet: Sheet = wb.createSheet("sheet1")
        var row: Row?
        var cell: Cell?
        var rowNum = 0
        val fileName = CommonUtil.getExcelFileName("notice_")

        // Header
        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("공지번호")
        cell = row.createCell(1)
        cell.setCellValue("제목")
        cell = row.createCell(2)
        cell.setCellValue("등록일시")
        cell = row.createCell(3)
        cell.setCellValue("비고")

        // Body
        page.content.forEach { noticeDTO ->
            row = sheet.createRow(rowNum++)
            cell = row?.createCell(0)
            cell?.setCellValue(noticeDTO.id.toString())
            cell = row?.createCell(1)
            cell?.setCellValue(noticeDTO.title)
            cell = row?.createCell(2)
            cell?.setCellValue(noticeDTO.createdAt.toString())
            cell = row?.createCell(3)
            cell?.setCellValue("")
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

}