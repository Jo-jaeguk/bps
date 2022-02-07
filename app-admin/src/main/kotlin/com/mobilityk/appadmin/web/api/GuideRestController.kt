package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.CommentType
import com.mobilityk.core.domain.DamageType
import com.mobilityk.core.domain.GuideNotificationSearch
import com.mobilityk.core.domain.GuidePrice
import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.buyandsell.ItemType
import com.mobilityk.core.dto.GuideCommentDTO
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.GuidePriceDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.guide.GuideCommentForAdminDTO
import com.mobilityk.core.dto.api.guide.GuideCommentVM
import com.mobilityk.core.dto.api.guide.GuidePriceCreateVM
import com.mobilityk.core.dto.api.guide.GuidePriceVM
import com.mobilityk.core.dto.api.guide.GuideUpdateVM
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.service.BuyAndSellService
import com.mobilityk.core.service.GuideCheckListService
import com.mobilityk.core.service.GuideCommentService
import com.mobilityk.core.service.GuideDamageService
import com.mobilityk.core.service.GuideNotificationService
import com.mobilityk.core.service.GuideOptionService
import com.mobilityk.core.service.GuidePriceService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.KakaoTalkService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.NewCarPriceService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class GuideRestController(
    private val guideService: GuideService,
    private val guidePriceService: GuidePriceService,
    private val guideCommentService: GuideCommentService,
    private val guideOptionService: GuideOptionService,
    private val guideDamageService: GuideDamageService,
    private val guideNotificationService: GuideNotificationService,
    private val guideCheckListService: GuideCheckListService,
    private val newCarPriceService: NewCarPriceService,
    private val memberService: MemberService,
    private val buyAndSellService: BuyAndSellService,
    private val kakaoTalkService: KakaoTalkService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/guide")
    fun getGuides(
        @RequestParam("search", required = false) search: String?,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<List<GuideDTO>>> {
        val searchOption = GuideSearchOption(
            search = search
        )
        return ResponseEntity.ok(
            ApiResponse<List<GuideDTO>>().apply {
                val page = guideService.findAllBySearchOption(searchOption, pageable)
                this.data = page.content
                this.isArray = true
                this.pagination = CommonUtil.convertPage(page)
            }
        )
    }

    @GetMapping("/guide/{id}")
    fun getGuideDetail(
        @PathVariable("id") id: Long,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<ApiResponse<GuideDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuideDTO>().apply {
                this.data = guideService.findByGuideIdForAdmin(id)
                this.data!!.options = guideOptionService.findAllByGuide(id)

                this.data!!.damages = guideDamageService.findAllByGuide(id)
                this.data!!.damageImages = guideService.findAllDamageImagesById(id)

                if(this.data!!.retailAvgPrice == null || this.data!!.bidAvgPrice == null) {
                    val tempDTO = guideService.updateRetailAvgPrice(this.data!!.id!!)
                    this.data!!.retailAvgPrice = tempDTO.retailAvgPrice
                    this.data!!.bidAvgPrice = tempDTO.bidAvgPrice
                }

                this.data!!.priceList = guidePriceService.findAllByGuideId(this.data!!.id!!)
                this.data!!.commentList = guideCommentService.findAllByGuideId(this.data!!.id!!, CommentType.GUIDE)
                this.data!!.additionalImages = guideService.findAllAdditionalImagesById(id)


                val guideSearch = GuideSearchOption(
                    carManufacturerName = this.data?.carManufacturer,
                    carModelName = this.data?.carModel,
                    carModelDetailName = this.data?.carModelDetail,
                    carTrimEq = this.data?.carTrim
                )
                val page = guideService.findAllBySearchOption(guideSearch, pageable)
                page.content.forEach {
                    it.convertData()
                    var buyTotalPrice = 0L
                    val buyList = buyAndSellService.getBuyListByGuideId(it.id!!)
                    if(!buyList.isNullOrEmpty()) {
                        val filtered = buyList.filter { it.itemType == ItemType.BUY_BASE_COST && it.name == "차량대금" }
                        if(!filtered.isNullOrEmpty()) {
                            it.carDaeGum = filtered[0].price!!
                        } else {
                            it.carDaeGum = 0L
                        }
                        buyList.forEach { buyDTO ->
                            buyTotalPrice += buyDTO.price!!
                        }

                    } else {
                        it.carDaeGum = 0L
                    }

                    val sellList = buyAndSellService.getSellListByGuideId(it.id!!)
                    if(!sellList.isNullOrEmpty()) {
                        it.sellPriceHistoryTb = sellList[0].sellPrice!! + sellList[0].otherCommission!!
                    } else {
                        it.sellPriceHistoryTb = 0L
                    }
                    it.price = if(it.price == null) 0L else it.price!! / 10000
                    it.carDaeGum = it.carDaeGum!! / 10000
                    it.sellRevenue = (it.sellPriceHistoryTb!! - buyTotalPrice) / 10000
                    it.sellPriceHistoryTb = it.sellPriceHistoryTb!! / 10000
                    it.newCarPrice = if(it.newCarPrice == null) 0L else it.newCarPrice!! / 10000
                }
                this.data!!.guideHistory = page.content

                val guideNotificationSearch = GuideNotificationSearch(
                    carManufacturerName = this.data!!.carManufacturer,
                    carModelName = this.data!!.carModel,
                    carModelDetailName = this.data!!.carModelDetail
                )
                val notiPage = guideNotificationService.findAllBySearch(
                    guideNotificationSearch,
                    PageRequest.of(0,3, Sort.Direction.DESC, "createdAt")
                )
                notiPage.content.forEach { it.convertData() }
                this.data!!.guideNotificationList = notiPage.content

                /*
                this.data!!.newCarPrice = newCarPriceService.getNewCarPrice(
                    this.data!!.carManufacturer!!,
                    this.data!!.carModel!!,
                    this.data!!.carModelDetail!!,
                    this.data!!.carTrim!!,
                )
                 */

            }
        )
    }


    @PutMapping("/guide/{id}")
    fun updateGuide(
        @PathVariable("id") id: Long,
        @RequestBody guideUpdateVM: GuideUpdateVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuideDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuideDTO>().apply {
                this.data = guideService.modifyGuide(
                    id,
                    user.username.toLong(),
                    guideUpdateVM
                )

                if(guideUpdateVM.guideStatus == GuideStatus.BUY) {
                    val memberDTO = memberService.findById(this.data?.memberId!!)
                    kakaoTalkService.sendKakaoTalk(
                        targetMemberDTO = memberDTO,
                        sendPhone = memberDTO.phone!!,
                        titleKey = "guide.sell.request.user.title",
                        messageKey = "guide.sell.request.user.body",
                        templateCodeKey = "guide.sell.request.user.template",
                        btnUrlKey = null,
                        btnTextKey = null,
                        messageList = listOf(this.data?.carNumber!!, CommonUtil.comma(this.data?.finalBuyPrice!!.toInt()))
                    )
                }
            }
        )
    }

    @PutMapping("/guide/{id}/send")
    fun sendGuide(
        @PathVariable("id") id: Long,
        @RequestBody guideDTO: GuideDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                try {
                    guideService.sendGuide(id, user.username.toLong(), guideDTO)
                } catch (e: Exception) {
                    this.message = e.message
                    this.result = -1
                }
            }
        )
    }

    @PutMapping("/guide/{id}/price")
    fun updateGuidePrice(
        @PathVariable("id") guideId: Long,
        @RequestBody guidePriceVM: GuidePriceCreateVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Long>> {
        return ResponseEntity.ok(
            ApiResponse<Long>().apply {
                this.data = guidePriceService.upsertGuidePrice(guideId, user.username.toLong(), guidePriceVM)
            }
        )
    }

    @PutMapping("/guide/{id}/price/one")
    fun updateGuidePriceOne(
        @PathVariable("id") guideId: Long,
        @RequestBody guidePriceVM: GuidePriceVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuidePriceDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuidePriceDTO>().apply {
                this.data = guidePriceService.upsertGuidePriceOne(guideId, user.username.toLong(), guidePriceVM)
            }
        )
    }

    @DeleteMapping("/guide/{id}")
    fun deleteGuide(
        @PathVariable("id") guideId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                try {
                    this.data = guideService.deleteGuideByAdmin(guideId, user.username.toLong())
                } catch (e: Exception) {
                    this.result = -1
                    this.message = e.message
                }
            }
        )
    }

    @PostMapping("/guide/{id}/comment")
    fun createComment(
        @PathVariable("id") guideId: Long,
        @RequestBody guideCommentVM: GuideCommentVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuideCommentDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuideCommentDTO>().apply {
                this.data = guideService.createGuideComment(guideId, user.username.toLong(), guideCommentVM)
            }
        )
    }

    @GetMapping("/guide/{id}/comment")
    fun getGuideComments(
        @PathVariable("id") guideId: Long
    ): ResponseEntity<ApiResponse<List<GuideCommentForAdminDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<GuideCommentForAdminDTO>>().apply {
                this.isArray = true
                this.data = guideService.findAllComments(guideId, CommentType.GUIDE)
            }
        )
    }

    @GetMapping("/guide/{id}/inspect")
    fun getInspect(
        @PathVariable("id") guideId: Long
    ): ResponseEntity<ApiResponse<GuideDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuideDTO>().apply {
                this.data = guideService.findInspectByGuideId(guideId)
                this.data!!.inspectCommentList = guideCommentService.findAllByGuideId(this.data!!.id!!, CommentType.INSPECT)
                this.data!!.additionalImages = guideService.findAllAdditionalImagesById(guideId)
                this.data!!.options = guideOptionService.findAllByGuide(guideId)
                this.data!!.damages = guideDamageService.findAllByGuide(guideId)

                this.data!!.damageImages = guideService.findAllDamageImagesById(guideId)
                this.data!!.priceList = guidePriceService.findAllByGuideId(this.data!!.id!!)
                this.data!!.commentList = guideCommentService.findAllByGuideId(this.data!!.id!!, CommentType.GUIDE)

                var damageTotalPrice = 0L
                var damageGiGyohwanCnt = 0L
                var damageGiPangumCnt = 0L
                var damageGiDosaekCnt = 0L
                var damageNeedGyohwanCnt = 0L
                var damageNeedPangumCnt = 0L
                var damageNeedDosaekCnt = 0L
                var damageOtherCnt = 0L
                var damageGiGyohwanPrice = 0L
                var damageGiPangumPrice = 0L
                var damageGiDosaekPrice = 0L
                var damageNeedGyohwanPrice = 0L
                var damageNeedPangumPrice = 0L
                var damageNeedDosaekPrice = 0L
                var damageOtherPrice = 0L

                this.data!!.damages?.forEach { guideDamageCheckDTO ->

                    when(guideDamageCheckDTO.damageType) {
                        DamageType.GI_GYOHWAN -> {
                            damageGiGyohwanCnt++
                            damageTotalPrice += guideDamageCheckDTO.price!!
                            damageGiGyohwanPrice += guideDamageCheckDTO.price!!
                        }
                        DamageType.GI_DOSAEK -> {
                            damageGiDosaekCnt++
                            damageTotalPrice += guideDamageCheckDTO.price!!
                            damageGiDosaekPrice += guideDamageCheckDTO.price!!
                        }
                        DamageType.GI_PANGUM -> {
                            damageGiPangumCnt++
                            damageTotalPrice += guideDamageCheckDTO.price!!
                            damageGiPangumPrice += guideDamageCheckDTO.price!!
                        }
                        DamageType.NEED_GYOHWAN -> {
                            damageNeedGyohwanCnt++
                            damageTotalPrice += guideDamageCheckDTO.price!!
                            damageNeedGyohwanPrice += guideDamageCheckDTO.price!!
                        }
                        DamageType.NEED_DOSAEK -> {
                            damageNeedDosaekCnt++
                            damageTotalPrice += guideDamageCheckDTO.price!!
                            damageNeedDosaekPrice += guideDamageCheckDTO.price!!
                        }
                        DamageType.NEED_PANGUM -> {
                            damageNeedPangumCnt++
                            damageTotalPrice += guideDamageCheckDTO.price!!
                            damageNeedPangumPrice += guideDamageCheckDTO.price!!
                        }
                        DamageType.OTHER -> {
                            damageOtherCnt++
                            damageTotalPrice += guideDamageCheckDTO.price!!
                            damageOtherPrice += guideDamageCheckDTO.price!!
                        }
                    }
                }

                this.data!!.damageTotalPrice = damageTotalPrice

                this.data!!.damageGiGyohwanCnt = damageGiGyohwanCnt
                this.data!!.damageGiGyohwanPrice = damageGiGyohwanPrice

                this.data!!.damageGiDosaekCnt = damageGiDosaekCnt
                this.data!!.damageGiDosaekPrice = damageGiDosaekPrice

                this.data!!.damageGiPangumCnt = damageGiPangumCnt
                this.data!!.damageGiPangumPrice = damageGiPangumPrice

                this.data!!.damageNeedGyohwanCnt = damageNeedGyohwanCnt
                this.data!!.damageNeedGyohwanPrice = damageNeedGyohwanPrice

                this.data!!.damageNeedDosaekCnt = damageNeedDosaekCnt
                this.data!!.damageNeedDosaekPrice = damageNeedDosaekPrice

                this.data!!.damageNeedPangumCnt = damageNeedPangumCnt
                this.data!!.damageNeedPangumPrice = damageNeedPangumPrice

                this.data!!.damageOtherCnt = damageOtherCnt
                this.data!!.damageOtherPrice = damageOtherPrice

                this.data!!.checkList = guideCheckListService.findAllByGuideId(guideId)


            }
        )
    }

    @DeleteMapping("/guide/{id}/inspect")
    fun deleteInspect(
        @PathVariable("id") guideId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = guideService.deleteInspect(guideId)
            }
        )
    }

    @PutMapping("/guide/{id}/inspect")
    fun updateInspect(
        @PathVariable("id") guideId: Long,
        @RequestBody guideDTO: GuideDTO
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = guideService.updateInspect(guideId, guideDTO)
            }
        )
    }

    @GetMapping("/guide/price/{id}")
    fun getGuidePriceDetail(
        @PathVariable("id") guidePriceId: Long,
    ): ResponseEntity<ApiResponse<GuidePriceDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuidePriceDTO>().apply {
                try {
                    this.data = guidePriceService.findByGuidePriceId(guidePriceId)
                } catch (e: CommException) {

                }
            }
        )
    }
}