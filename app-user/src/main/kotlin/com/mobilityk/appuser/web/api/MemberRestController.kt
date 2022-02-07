package com.mobilityk.appuser.web.api

import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.MemberDetailDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.member.MemberJoinVM
import com.mobilityk.core.dto.api.member.MemberVM
import com.mobilityk.core.dto.api.member.PasswordVM
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.KakaoService
import com.mobilityk.core.service.KakaoTalkService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.SecurityLoginService
import com.mobilityk.core.util.CONST
import mu.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import java.util.Locale
import javax.servlet.http.HttpSession
import javax.validation.Valid

@RestController
@RequestMapping("/api/user/v1")
@Validated
data class MemberRestController(
    private val memberService: MemberService,
    private val securityLoginService: SecurityLoginService,
    private val guideService: GuideService,
    private val kakaoTalkService: KakaoTalkService,
    private val messageSource: MessageSource
) {

    private val logger = KotlinLogging.logger {}

    private fun getMessage(): List<String>{
        val list = mutableListOf<String>()
        for (i in 0 .. 14) {
            list.add(i.toString())
        }
        return list
    }

    @PostMapping("/member/join")
    fun createMember(
        @Valid @RequestBody memberVM: MemberJoinVM,
        httpSession: HttpSession
    ): ResponseEntity<ApiResponse<Long>> {
        var emailId: Long? = -1L
        var result = 0
        var message = ""
        try {
            emailId = memberService.createEmailAccount(memberVM.emailAddress!!, memberVM.password!!)
            /*
            kakaoTalkService.sendKakaoTalk(
                "01032228237",
                messageSource.getMessage("guide.request.body", getMessage().toTypedArray(), Locale.getDefault()),
                messageSource.getMessage("guide.request.template", emptyArray<String>(), Locale.getDefault()),
                null,
                null
            )
             */
        } catch (e: Exception) {
            result = -1
            message = e.message.toString()
        }
        /*
        val securityMember = securityLoginService.loadUserByUsername(member.emailAddress)
        securityMember?.let {
            val authentication = UsernamePasswordAuthenticationToken(it, it.password, it.authorities)
            val securityContext = SecurityContextHolder.getContext()
            securityContext.authentication = authentication
            httpSession.setAttribute("SPRING_SECURITY_CONTEXT" , securityContext)
        }
         */
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<Long>().apply {
                this.data = emailId
                this.result = result
                this.message = message
            }
        )
    }

    @GetMapping("/member/me")
    fun getMyInfo(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDetailDTO>> {
        val memberId = user.username.toLong()
        var searchOption = GuideSearchOption(
            memberId = memberId
        )
        val memberDetailDTO = MemberDetailDTO()
        memberDetailDTO.member = memberService.findById(memberId)

        var resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        memberDetailDTO.totalGuideCnt = resultMap[CONST.COUNT]

        searchOption.guideStatusList = listOf(GuideStatus.FINISH)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        memberDetailDTO.finishGuideCnt = resultMap[CONST.COUNT]

        searchOption.guideStatusList = listOf(GuideStatus.REQUEST, GuideStatus.BUY, GuideStatus.ING, GuideStatus.SELLING)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        memberDetailDTO.ingGuideCnt = resultMap[CONST.COUNT]

        searchOption.guideStatusList = listOf(GuideStatus.BUY)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        memberDetailDTO.buyGuideCnt = resultMap[CONST.COUNT]
        memberDetailDTO.buyGuideMoney = resultMap[CONST.MONEY]

        searchOption.guideStatusList = listOf(GuideStatus.SELLING)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        memberDetailDTO.sellingGuideCnt = resultMap[CONST.COUNT]
        memberDetailDTO.sellingGuideMoney = resultMap[CONST.MONEY]

        searchOption.guideStatusList = listOf(GuideStatus.SELL)
        resultMap = guideService.getGuideAggregationBySearchOption(searchOption)
        memberDetailDTO.sellGuideCnt = resultMap[CONST.COUNT]
        memberDetailDTO.sellGuideMoney = resultMap[CONST.MONEY]

        logger.info { "memberDetail ${memberDetailDTO}" }

        return ResponseEntity.ok(
            ApiResponse<MemberDetailDTO>().apply {
                this.data = memberDetailDTO
            }
        )
    }


    @PutMapping("/member/password")
    fun updatePassword(
        @Valid @RequestBody passwordVM: PasswordVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.updatePassword(user.username.toLong(), passwordVM)
            }
        )
    }

    @PutMapping("/member/{memberId}/change/password")
    fun changePassword(
        @PathVariable("memberId") memberId: Long,
        @Valid @RequestBody passwordVM: PasswordVM
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.updatePassword(memberId, passwordVM)
            }
        )
    }

    @PutMapping("/member")
    fun updateMyInfo(
        @Valid @RequestBody memberVM: MemberVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.updateMemberInfo(user.username.toLong(), null, memberVM)
            }
        )
    }




}