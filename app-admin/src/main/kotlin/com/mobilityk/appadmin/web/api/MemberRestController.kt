package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.dto.MemberCommentDTO
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.MemberDetailDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.member.MemberJoinVM
import com.mobilityk.core.dto.api.member.MemberVM
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.MemberCommentService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.util.CONST
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class MemberRestController(
    private val memberService: MemberService,
    private val memberCommentService: MemberCommentService,
    private val guideService: GuideService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/member/{memberId}")
    fun getMemberDetail(
        @PathVariable("memberId") memberId: Long
    ): ResponseEntity<ApiResponse<MemberDetailDTO>> {

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

        searchOption.guideStatusList = listOf(GuideStatus.ING, GuideStatus.REQUEST, GuideStatus.BUY, GuideStatus.SELLING)
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

    @PostMapping("/member/admin")
    fun createAdmin(
        @RequestBody memberVM: MemberVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.ok(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.createAdmin(user.username.toLong(), memberVM)
            }
        )
    }

    @PutMapping("/member/{memberId}/point")
    fun updateMemberPoint(
        @PathVariable("memberId") memberId: Long,
        @RequestBody memberVM: MemberVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.ok(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.updatePoint(memberId, user.username.toLong(), memberVM)
            }
        )
    }

    @PutMapping("/member/{memberId}/password")
    fun updateMemberPassword(
        @PathVariable("memberId") memberId: Long,
        @RequestBody memberVM: MemberVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.ok(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.updatePassword(memberId, user.username.toLong(), memberVM)
            }
        )
    }


    @PutMapping("/member/{memberId}/update")
    fun updateMember(
        @PathVariable("memberId") memberId: Long,
        @RequestBody memberVM: MemberVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.ok(
            ApiResponse<MemberDTO>().apply {
                try {
                    this.data = memberService.updateMemberInfo(memberId, user.username.toLong(), memberVM)
                } catch (e: Exception) {
                    this.result = -1
                    this.message = e.message
                }
            }
        )
    }

    @PutMapping("/member/{memberId}/deactivate")
    fun deActivateMember (
        @PathVariable("memberId") memberId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.ok(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.deActivate(memberId, user.username.toLong())
            }
        )
    }

    @PutMapping("/member/{memberId}/role")
    fun updateRoleMember(
        @PathVariable("memberId") memberId: Long,
        @RequestBody memberVM: MemberVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberDTO>> {
        return ResponseEntity.ok(
            ApiResponse<MemberDTO>().apply {
                this.data = memberService.updateMemberRole(memberId, user.username.toLong(), memberVM)
            }
        )
    }

    @PostMapping("/member/{memberId}/comment")
    fun createComment(
        @PathVariable("memberId") memberId: Long,
        @RequestBody memberCommentDTO: MemberCommentDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<MemberCommentDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<MemberCommentDTO>().apply {
                this.data = memberCommentService.createComment(memberId, memberCommentDTO, user.username.toLong())
            }
        )
    }

    @DeleteMapping("/member/{memberId}/comment/{commentId}")
    fun deleteComment(
        @PathVariable("memberId") memberId: Long,
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = memberCommentService.deleteComment(memberId, commentId)
            }
        )
    }

}