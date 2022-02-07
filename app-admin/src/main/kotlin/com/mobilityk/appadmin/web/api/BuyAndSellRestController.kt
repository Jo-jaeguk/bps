package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.buyandsell.BuyItem
import com.mobilityk.core.domain.buyandsell.ItemType
import com.mobilityk.core.dto.BuyTypeDTO
import com.mobilityk.core.dto.NewCarDealerLocationDTO
import com.mobilityk.core.dto.NewCarDealerNameDTO
import com.mobilityk.core.dto.buyandsell.BuyAndSellDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.buyandsell.BuyItemDTO
import com.mobilityk.core.dto.buyandsell.CarLocationDTO
import com.mobilityk.core.dto.buyandsell.SellTargetDTO
import com.mobilityk.core.dto.buyandsell.SellerDTO
import com.mobilityk.core.service.BuyAndSellService
import com.mobilityk.core.service.MemberService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class BuyAndSellRestController(
    private val buyAndSellService: BuyAndSellService,
    private val memberService: MemberService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/buyandsell/{id}")
    fun detail(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<BuyAndSellDTO>> {
        return ResponseEntity.ok(
            ApiResponse<BuyAndSellDTO>().apply {
                this.data = buyAndSellService.findById(id)

                this.data!!.admins = memberService.getAdmins()
                this.data!!.buyTypeList = buyAndSellService.getBuyTypes()

                this.data!!.buyList = buyAndSellService.getBuyList(this.data!!.id!!)
                this.data!!.sellList = buyAndSellService.getSellList(this.data!!.id!!)

                var maxOrderIndex = 0
                this.data!!.sellList?.forEach { sellDTO ->
                    if(maxOrderIndex < sellDTO.orderIndex!!) {
                        maxOrderIndex = sellDTO.orderIndex!!
                    }
                }
                this.data!!.maxSellOrderIndex = maxOrderIndex

                this.data!!.buyBaseCostList = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_BASE_COST)
                this.data!!.buyBidPriceList = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_BID_PRICE)
                this.data!!.buyProdPriceList = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_PROD_PRICE)
                this.data!!.buyRetailPriceList = buyAndSellService.getBuyItemsByItemType(ItemType.BUY_RETAIL_PRICE)

                this.data!!.newCardealerNames = buyAndSellService.getnewCarDealerNames()
                this.data!!.newCarDealerLocations = buyAndSellService.getnewCarDealerLocations()
                this.data!!.carLocations = buyAndSellService.getCarLocations()

                this.data!!.sellTargets = buyAndSellService.getSellTargets()

                this.data!!.sellers = buyAndSellService.getSellers()


                this.data!!.convertData()
            }
        )
    }

    @PostMapping("/buyandsell/{id}")
    fun saveBuyAndSell(
        @PathVariable("id") id: Long,
        @RequestBody buyAndSellDTO: BuyAndSellDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<BuyAndSellDTO>> {
        return ResponseEntity.ok(
            ApiResponse<BuyAndSellDTO>().apply {
                this.data = buyAndSellService.saveBuyAndSell(id, buyAndSellDTO)
                buyAndSellService.updateBuyAndSellPrice(id)
            }
        )
    }


    @PostMapping("/buyandsell/item")
    fun createBuyItem(
        @RequestBody buyItemDTO: BuyItemDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<BuyItemDTO>> {
        return ResponseEntity.ok(
            ApiResponse<BuyItemDTO>().apply {
                this.data = buyAndSellService.createBuyItem(buyItemDTO)
            }
        )
    }

    @GetMapping("/buyandsell/item")
    fun getBuyItems(
        @RequestParam("item_type") itemType: ItemType,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<BuyItemDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<BuyItemDTO>>().apply {
                this.data = buyAndSellService.getBuyItemsByItemType(itemType)
            }
        )
    }

    @DeleteMapping("/buyandsell/item/{id}")
    fun deleteBuyItems(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = buyAndSellService.deleteBuyItem(id)
            }
        )
    }

    @PostMapping("/buyandsell/buy_type")
    fun createBuyType(
        @RequestBody buyTypeDTO: BuyTypeDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<BuyTypeDTO>> {
        return ResponseEntity.ok(
            ApiResponse<BuyTypeDTO>().apply {
                this.data = buyAndSellService.createBuyType(buyTypeDTO)
            }
        )
    }

    @GetMapping("/buyandsell/buy_type")
    fun getBuyTypes(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<BuyTypeDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<BuyTypeDTO>>().apply {
                this.data = buyAndSellService.getBuyTypes()
            }
        )
    }

    @DeleteMapping("/buyandsell/buy_type/{id}")
    fun deleteBuyType(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = buyAndSellService.deleteBuyType(id)
            }
        )
    }

    @PostMapping("/buyandsell/dealer/name")
    fun createDealerName(
        @RequestBody newCarDealerNameDTO: NewCarDealerNameDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<NewCarDealerNameDTO>> {
        return ResponseEntity.ok(
            ApiResponse<NewCarDealerNameDTO>().apply {
                this.data = buyAndSellService.createNewCarDealerName(newCarDealerNameDTO)
            }
        )
    }

    @GetMapping("/buyandsell/dealer/name")
    fun getDealerNames(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<NewCarDealerNameDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<NewCarDealerNameDTO>>().apply {
                this.data = buyAndSellService.getnewCarDealerNames()
            }
        )
    }

    @DeleteMapping("/buyandsell/dealer/name/{id}")
    fun deleteDealerName(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = buyAndSellService.deleteNewCarDealerNames(id)
            }
        )
    }

    @PostMapping("/buyandsell/dealer/location")
    fun createDealerLocation(
        @RequestBody newCarDealerLocationDTO: NewCarDealerLocationDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<NewCarDealerLocationDTO>> {
        return ResponseEntity.ok(
            ApiResponse<NewCarDealerLocationDTO>().apply {
                this.data = buyAndSellService.createNewCarDealerLocation(newCarDealerLocationDTO)
            }
        )
    }

    @GetMapping("/buyandsell/dealer/location")
    fun getDealerLocation(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<NewCarDealerLocationDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<NewCarDealerLocationDTO>>().apply {
                this.data = buyAndSellService.getnewCarDealerLocations()
            }
        )
    }

    @DeleteMapping("/buyandsell/dealer/location/{id}")
    fun deleteDealerLocation(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = buyAndSellService.deleteNewCarDealerLocation(id)
            }
        )
    }

    @PostMapping("/buyandsell/car_location")
    fun createCarLocation(
        @RequestBody carLocationDTO: CarLocationDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<CarLocationDTO>> {
        return ResponseEntity.ok(
            ApiResponse<CarLocationDTO>().apply {
                this.data = buyAndSellService.createCarLocation(carLocationDTO)
            }
        )
    }

    @GetMapping("/buyandsell/car_location")
    fun getCarLocation(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<CarLocationDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<CarLocationDTO>>().apply {
                this.data = buyAndSellService.getCarLocations()
            }
        )
    }

    @DeleteMapping("/buyandsell/car_location/{id}")
    fun deleteCarLocation(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = buyAndSellService.deleteCarLocation(id)
            }
        )
    }

    @PostMapping("/buyandsell/sell_target")
    fun createSellTarget(
        @RequestBody sellTargetDTO: SellTargetDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<SellTargetDTO>> {
        return ResponseEntity.ok(
            ApiResponse<SellTargetDTO>().apply {
                this.data = buyAndSellService.createSellTarget(sellTargetDTO)
            }
        )
    }

    @GetMapping("/buyandsell/sell_target")
    fun getSellTarget(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<SellTargetDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<SellTargetDTO>>().apply {
                this.data = buyAndSellService.getSellTargets()
            }
        )
    }

    @DeleteMapping("/buyandsell/sell_target/{id}")
    fun deleteSellTarget(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = buyAndSellService.deleteSellTarget(id)
            }
        )
    }


    @PostMapping("/buyandsell/seller")
    fun createSeller(
        @RequestBody sellerDTO: SellerDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<SellerDTO>> {
        return ResponseEntity.ok(
            ApiResponse<SellerDTO>().apply {
                this.data = buyAndSellService.createSeller(sellerDTO)
            }
        )
    }

    @GetMapping("/buyandsell/seller")
    fun getSeller(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<SellerDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<SellerDTO>>().apply {
                this.data = buyAndSellService.getSellers()
            }
        )
    }

    @DeleteMapping("/buyandsell/seller/{id}")
    fun deleteSeller(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = buyAndSellService.deleteSeller(id)
            }
        )
    }

}