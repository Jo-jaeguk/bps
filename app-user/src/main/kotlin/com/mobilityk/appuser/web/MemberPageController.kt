package com.mobilityk.appuser.web

import com.mobilityk.core.repository.search.MemberSearchOption
import com.mobilityk.core.service.MemberService
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/user")
data class MemberPageController(
    private val memberService: MemberService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/member")
    @ResponseBody
    fun members(
        pageable: Pageable
    ): String {
        val searchOption = MemberSearchOption(
            email = "1",
            createdAt = null
        )
        val pageing = memberService.findAllBySearch(searchOption, pageable)
        return pageing.content.toString()
    }
}