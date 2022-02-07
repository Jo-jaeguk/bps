package com.mobilityk.appuser.web.api

import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.GuideDamageCheckDTO
import com.mobilityk.core.dto.UploadDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.guide.GuideCreateVM
import com.mobilityk.core.dto.api.guide.GuideInspectSaveVM
import com.mobilityk.core.dto.api.guide.GuideInspectVM
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.service.DamageCheckService
import com.mobilityk.core.service.GuideOptionService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.KakaoTalkService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.UploadService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/user/v1")
@Validated
data class GuideRestController(
    private val memberService: MemberService,
    private val guideService: GuideService,
    private val kakaoTalkService: KakaoTalkService,
    private val messageSource: MessageSource,
    private val memberMapper: MemberMapper,
    private val damageCheckService: DamageCheckService,
    private val guideOptionService: GuideOptionService,
    private val uploadService: UploadService
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/guide")
    fun guide(
        @RequestParam("guide_status", required = false) guideStatus: GuideStatus?,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<List<GuideDTO>>> {
        val searchOption = GuideSearchOption(
            guideStatus = guideStatus
        )
        return ResponseEntity.ok(ApiResponse<List<GuideDTO>>().apply {
            isArray = true
            val page = guideService.findAllBySearchOption(searchOption, pageable)
            this.data = page.content
            this.pagination = CommonUtil.convertPage(page)
        })
    }

    @GetMapping("/guide/{id}")
    fun getDetail(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuideDTO>> {
        return ResponseEntity.ok(ApiResponse<GuideDTO>().apply {
            this.data = guideService.findByGuideId(id, user.username.toLong())
        })
    }

    @PostMapping("/guide")
    fun createGuide(
        @Valid @RequestBody guideVM: GuideCreateVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuideDTO>> {

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<GuideDTO>().apply {
                val guideDTO = guideService.createGuide(guideVM, user.username.toLong())
                this.data = guideDTO

                memberService.increasePoint(guideDTO.memberId!!, 1000)

                val memberDTO = memberService.findById(guideDTO.memberId!!)

                if(memberDTO.kakaoTalkYn != null && memberDTO.kakaoTalkYn!!) {
                    val guideOptions = guideOptionService.findAllByGuide(guideDTO.id!!)
                    kakaoTalkService.sendKakaoTalkToAdmin(
                        memberDTO = memberDTO,
                        titleKey = "guide.request.admin.title",
                        messageKey = "guide.request.admin.body",
                        templateCodeKey = "guide.request.admin.template",
                        btnUrlKey = "guide.request.admin.btn.url",
                        btnTextKey = "guide.request.admin.btn.text",
                        messageList = CommonUtil.getGuideKakaoAdminSendMessage(
                            guideDTO = guideDTO,
                            memberDTO = memberDTO,
                            guideOptions = guideOptions
                        ),
                        urlList = listOf("admin/guide/detail/${guideDTO.id}")
                    )
                    kakaoTalkService.sendKakaoTalk(
                        targetMemberDTO = memberDTO,
                        sendPhone = memberDTO.phone!!,
                        titleKey = "guide.request.title",
                        messageKey = "guide.request.body",
                        templateCodeKey = "guide.request.template",
                        btnUrlKey = null,
                        btnTextKey = null,
                        messageList = CommonUtil.getGuideKakaoUserReqMessage(
                            guideDTO = guideDTO,
                            guideOptions = guideOptions
                        )
                    )
                }

            }
        )
    }

    @PostMapping("/guide/{id}")
    fun updateGuide(
        @PathVariable("id") id: Long,
        @Valid @RequestBody guideVM: GuideCreateVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuideDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<GuideDTO>().apply {
                this.data = guideService.updateGuide(id, guideVM, user.username.toLong())
            }
        )
    }

    @DeleteMapping("/guide/{id}")
    fun deleteGuide(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<Unit>().apply {
                this.data = guideService.deleteGuideByUser(id, user.username.toLong())
            }
        )
    }

    @PostMapping("/guide/car_image")
    fun uploadImage(
        @RequestParam(name = "image_1", required = false) image1: MultipartFile?,
        @RequestParam(name = "image_2", required = false) image2: MultipartFile?,
        @RequestParam(name = "image_3", required = false) image3: MultipartFile?,
        @RequestParam(name = "image_4", required = false) image4: MultipartFile?,
        @RequestParam(name = "image_5", required = false) image5: MultipartFile?,
        @RequestParam(name = "guideTempId") guideTempId: String,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.info { "guideTempId $guideTempId" }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<Unit>().apply {
                guideService.createGuideCarImages(
                    image1 = image1,
                    image2 = image2,
                    image3 = image3,
                    image4 = image4,
                    image5 = image5,
                    guideTempId = guideTempId.toLong(),
                    memberId = user.username.toLong(),
                    httpServletRequest
                )
            }
        )
    }

    @PostMapping("/guide/additional/car_image")
    fun uploadAdditionalImage(
        @RequestParam(name = "image_1", required = false) image1: MultipartFile?,
        @RequestParam(name = "image_2", required = false) image2: MultipartFile?,
        @RequestParam(name = "image_3", required = false) image3: MultipartFile?,
        @RequestParam(name = "image_4", required = false) image4: MultipartFile?,
        @RequestParam(name = "image_5", required = false) image5: MultipartFile?,
        @RequestParam(name = "guideId") guideId: String,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.info { "guideId $guideId" }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<Unit>().apply {
                // 추가 사진은 신규로 계속 업데이트 delete -> insert
                guideService.deleteAllAdditionalImages(guideId.toLong(), user.username.toLong())
                guideService.createGuideAdditionalCarImages(
                    image1 = image1,
                    image2 = image2,
                    image3 = image3,
                    image4 = image4,
                    image5 = image5,
                    guideId = guideId.toLong(),
                    memberId = user.username.toLong(),
                    httpServletRequest
                )
            }
        )
    }

    @PostMapping("/guide/{guideId}/car_inspect/image/{type}")
    fun saveInspectImg(
        @RequestParam param: MutableMap<Any, Any>,
        @PathVariable("guideId") guideId: Long,
        @PathVariable("type") type: String,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<ApiResponse<UploadDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<UploadDTO>().apply {
                this.data = guideService.creteGuideInspectImage(
                    type,
                    httpServletRequest.getParameter("imgSrc").toString(),
                    guideId,
                    user.username.toLong(),
                    httpServletRequest
                )
            }
        )
    }

    @PostMapping("/guide/{guideId}/car_inspect")
    @Transactional
    fun saveInspect(
        @RequestBody inspectSaveVM: GuideInspectSaveVM,
        @PathVariable("guideId") guideId: Long,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<ApiResponse<GuideDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<GuideDTO>().apply {
                guideService.deleteGuideOptions(guideId)
                guideService.deleteGuideCheckList(guideId)
                guideService.deleteGuideDamages(guideId)

                this.data = guideService.saveInpect(guideId, inspectSaveVM, user.username.toLong()!!)
            }
        )
    }

    @PostMapping("/guide/{guideId}/damage")
    fun createDamage(
        @PathVariable("guideId") guideId: Long,
        @RequestBody damageCheckDTO: GuideDamageCheckDTO,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<ApiResponse<GuideDamageCheckDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<GuideDamageCheckDTO>().apply {
                this.data = damageCheckService.createDamage(
                    guideId = guideId,
                    carType = damageCheckDTO.carType!!,
                    damageType = damageCheckDTO.damageType!!,
                    damageLocation = damageCheckDTO.damageLocation!!
                )
            }
        )
    }

    @PostMapping("/guide/minus_price")
    fun getInspectMinusPrice(
        @RequestBody guideInspectVM: GuideInspectVM,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<ApiResponse<GuideInspectVM>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<GuideInspectVM>().apply {
                this.data = guideService.getInspectMinusPrice(guideInspectVM)
            }
        )
    }

    @DeleteMapping("/guide/{id}/reset_damage")
    fun resetDamage(
        @PathVariable("id") guideId: Long,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<Unit>().apply {
                this.data = guideService.resetDamageCheck(guideId)
            }
        )
    }

    @PostMapping("/guide/{guideId}/request_buy")
    fun requestBuy(
        @PathVariable("guideId") guideId: Long,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<Unit>().apply {
                val guideDTO = guideService.requestBuy(guideId)
                val memberDTO = memberService.findById(user.username.toLong())
                val guideOptions = guideOptionService.findAllByGuide(guideDTO.id!!)
                kakaoTalkService.sendKakaoTalkToAdmin(
                    memberDTO = memberDTO,
                    titleKey = "guide.sell.request.admin.title",
                    messageKey = "guide.sell.request.admin.body",
                    templateCodeKey = "guide.sell.request.admin.template",
                    btnUrlKey = null,
                    btnTextKey = null,
                    messageList = CommonUtil.getGuideKakaoAdminBuyReqSendMessage(
                        guideDTO = guideDTO,
                        memberDTO = memberDTO,
                        guideOptions = guideOptions
                    ),
                    urlList = null
                )
            }
        )
    }

}