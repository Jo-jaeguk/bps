package com.mobilityk.appadmin.web

import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.repository.search.MemberSearchOption
import com.mobilityk.core.service.BranchService
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
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDateTime
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin")
data class MemberPageController(
    private val memberService: MemberService,
    private val branchService: BranchService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/member")
    fun members(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String , Any>
    ): ModelAndView {
        val searchOption = MemberSearchOption(
            search = if(search.isNullOrEmpty()) null else search,
            //notInRole = ROLE.MASTER
        )
        val page = memberService.findAllBySearch(searchOption, pageable)
        model["link"] = "member"
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        model["search"] = search ?: ""
        model["total_count"] = memberService.count()
        model["branch_list"] = branchService.findAll()
        return ModelAndView("member/member" , model)
    }

    @GetMapping("/member/excel/download")
    fun getExcel(
        @RequestParam("search", required = false) search: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        model: MutableMap<String , Any>,
        response: HttpServletResponse
    ): Unit {
        val wb: Workbook = XSSFWorkbook()
        val sheet: Sheet = wb.createSheet("첫번째 시트")
        var row: Row?
        var cell: Cell?
        var rowNum = 0
        val fileName = CommonUtil.getExcelFileName("member_")

        // Header
        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("회원번호")
        cell = row.createCell(1)
        cell.setCellValue("아이디")
        cell = row.createCell(2)
        cell.setCellValue("이름")
        cell = row.createCell(3)
        cell.setCellValue("연락처")
        cell = row.createCell(4)
        cell.setCellValue("요청건수")
        cell = row.createCell(5)
        cell.setCellValue("권한")
        cell = row.createCell(6)
        cell.setCellValue("가입일시")

        // Body
        val searchOption = MemberSearchOption(
            search = search
        )
        val page = memberService.findAllBySearch(searchOption, pageable)
        page.content.forEach { memberDTO ->
            row = sheet.createRow(rowNum++)
            cell = row?.createCell(0)
            cell?.setCellValue(memberDTO.id.toString())
            cell = row?.createCell(1)
            cell?.setCellValue(memberDTO.emailAddress)
            cell = row?.createCell(2)
            cell?.setCellValue(memberDTO.name)
            cell = row?.createCell(3)
            cell?.setCellValue(memberDTO.phone)
            cell = row?.createCell(4)
            cell?.setCellValue(memberDTO.guideCount.toString())
            cell = row?.createCell(5)
            var memberRole = ""
            memberDTO.roles?.forEach { role ->
                memberRole = role.description
            }
            cell?.setCellValue(memberRole)
            cell = row?.createCell(6)
            cell?.setCellValue(memberDTO.createdAt.toString())
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