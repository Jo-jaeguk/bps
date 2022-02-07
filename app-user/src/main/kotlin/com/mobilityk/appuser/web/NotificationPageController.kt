package com.mobilityk.appuser.web

import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.NotificationService
import com.mobilityk.core.util.CONST
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
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/user")
data class NotificationPageController(
    private val memberService: MemberService,
    private val notificationService: NotificationService
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/notification")
    fun mypage(
        model: MutableMap<String, Any>,
        @PageableDefault(page = 0, size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        @AuthenticationPrincipal user: User
    ): ModelAndView {
        model["link"] = "notification"
        val page = notificationService.findAllByMemberId(user.username.toLong(), pageable)
        model["content"] = page.content
        val pagination = CommonUtil.convertPage(page)
        model["page"] = pagination
        return ModelAndView("notification/notification_list" , model)
    }

    @GetMapping("/notification/{id}")
    fun notiDetail(
        @PathVariable("id")id: Long,
        model: MutableMap<String, Any>,
        @AuthenticationPrincipal user: User
    ): ModelAndView {
        model["link"] = "notification"
        model["data"] = notificationService.findById(user.username.toLong(), id)
        return ModelAndView("notification/notification_detail" , model)
    }


}