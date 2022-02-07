package com.mobilityk.appuser.web

import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.util.CONST
import mu.KotlinLogging
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/user")
data class MyPageController(
    private val memberService: MemberService,
    private val guideService: GuideService
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/mypage")
    fun mypage(
        @RequestParam("error" , required = false) error: String?,
        model: MutableMap<String, Any>,
        @AuthenticationPrincipal user: User
    ): ModelAndView {

        logger.info { "/mypage error [$error]" }

        model["link"] = "mypage"
        model["member"] = memberService.findById(user.username.toLong())
        val searchOption = GuideSearchOption()

        searchOption.memberId = user.username.toLong()

        var resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        model["total_count"] = resultMap[CONST.COUNT].toString()

        searchOption.guideStatusList = listOf(GuideStatus.FINISH)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        model["finish_count"] = resultMap[CONST.COUNT].toString()

        searchOption.guideStatusList = listOf(GuideStatus.SELL)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        model["sell_count"] = resultMap[CONST.COUNT].toString()

        searchOption.guideStatusList = listOf(GuideStatus.REQUEST, GuideStatus.SELLING, GuideStatus.BUY, GuideStatus.ING)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        model["ing_count"] = resultMap[CONST.COUNT].toString()

        return ModelAndView("mypage/mypage" , model)
    }

    @GetMapping("/mypage/change/password")
    fun changePassword(
        model: MutableMap<String, Any>,
        @AuthenticationPrincipal user: User
    ): ModelAndView {
        return ModelAndView("mypage/change_password" , model)
    }
}