package com.mobilityk.appadmin.web

import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.MemberService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDateTime

@Controller
@RequestMapping("/admin")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class DashBoardPageController(
    private val memberService: MemberService,
    private val guideService: GuideService
) {
    @GetMapping("/dashboard")
    fun home(
        model: MutableMap<String , Any>
    ): ModelAndView {
        val now = LocalDateTime.now()
        //model["data"] = guideService.getDashBoard()
        model["link"] = "dashboard"
        model["today"] = now.year.toString() + "년 " + now.monthValue + "월 " + now.dayOfMonth + "일"
        model["year"] = now.year.toString()
        model["month"] =  now.monthValue
        model["date"] = now.dayOfMonth

        return ModelAndView("dashboard/dashboard" , model)
    }
}