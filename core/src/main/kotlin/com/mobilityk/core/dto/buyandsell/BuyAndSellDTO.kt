package com.mobilityk.core.dto.buyandsell

import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.dto.BuyTypeDTO
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.NewCarDealerLocationDTO
import com.mobilityk.core.dto.NewCarDealerNameDTO
import org.springframework.format.annotation.DateTimeFormat
import java.time.Duration
import java.time.LocalDateTime
import javax.persistence.Column

data class BuyAndSellDTO(
    var id: Long? = null,

    var guide: GuideDTO? = null,

    var member: MemberDTO? = null,

    var buyer: MemberDTO? = null,

    var guideId: Long? = null,

    var buyerId: Long? = null,

    var stockDuration: Long? = null,

    var successedAtStr: String? = null,

    var depositedAtStr: String? = null,

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var successedAt: LocalDateTime? = null,

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var depositedAt: LocalDateTime? = null,

    var buyType: String? = null,

    var modifyCarNumber: String? = null,

    var modifyCarNumberTb: String? = null,

    var newCarDealerLocation: String? = null,

    var seller: String? = null,

    var carLocation: String? = null,

    var newCarDealerName: String? = null,

    var buyDate: LocalDateTime? = null,

    var buyDateStr: String? = null,

    var sellDate: LocalDateTime? = null,

    var admins: List<MemberDTO>? = null,

    var buyTypeList: List<BuyTypeDTO>? = null,

    var newCarDealerLocations: List<NewCarDealerLocationDTO>? = null,

    var newCardealerNames: List<NewCarDealerNameDTO>? = null,

    var carLocations: List<CarLocationDTO>? = null,

    var sellTargets: List<SellTargetDTO>? = null,

    var sellers: List<SellerDTO>? = null,

    var buyList: List<BuyDTO>? = null,

    var sellList: List<SellDTO>? = null,

    var buyBaseCostList: List<BuyItemDTO>? = null,
    var buyBidPriceList: List<BuyItemDTO>? = null,
    var buyProdPriceList: List<BuyItemDTO>? = null,
    var buyRetailPriceList: List<BuyItemDTO>? = null,

    var maxSellOrderIndex: Int? = null,
    var incomeYn: EnumYn? = null,
    var needDocument: EnumYn? = null,

    // ?????????
    var bidSuccessPrice: Long? = null,

    // ???????????? ???
    var sellTotalPrice: Long? = null,

    // ???????????????
    var sellExpectPrice: Long? = null,

    // ?????????
    var sellSellPrice: Long? = null,

    // ???????????????
    var sellHopePrice: Long? = null,

    // ?????? ????????? ??????
    var sellOtherCommission: Long? = null,

    // ????????? ??????
    var sellDiffPrice: Long? = null,

    // ???????????? ???
    var buyTotalPrice: Long? = null,

    // ???????????? ??? ????????? ?????????
    var buyTotalPriceTb: Long? = null,

    // ????????????
    var expectRevenue: Long? = null,

    // ????????????
    var sellRevenue: Long? = null,

    // ???????????? ??????
    var sellRevenueDiff: Long? = null,

    // ????????? ??????????????? ???
    var sellTotalPriceTb: Long? = null,

    // ???????????? total sum
    var totalBuyItemPrice: Long? = null,

    // ????????????
    var carPrice: Long? = null,

    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {
    fun convertData() {
        this.stockDuration = if(this.depositedAt != null) {
//            ??????????????? sellDate ??? ?????? depositedAt ??? ???????????????.
//            Duration.between(this.buyDate, this.sellDate).toDays()
            Duration.between(this.buyDate, this.depositedAt).toDays()
        } else {
            0L
        }
        this.guide?.guideStatusStr = this.guide?.guideStatus?.description

        if(this.successedAt != null) {
            this.successedAtStr = this.successedAt?.year.toString() + "-" +
                this.successedAt?.monthValue + "-" + this.successedAt?.dayOfMonth
        } else {
            this.successedAtStr = ""
        }

        if(this.depositedAt != null) {
            this.depositedAtStr = this.depositedAt?.year.toString() + "-" +
                this.depositedAt?.monthValue + "-" + this.depositedAt?.dayOfMonth
        } else {
            this.depositedAtStr = ""
        }

        if(this.buyDate != null) {
            this.buyDateStr = this.buyDate?.year.toString() + "-" +
                this.buyDate?.monthValue + "-" + this.buyDate?.dayOfMonth
        } else {
            this.buyDateStr = ""
        }

        this.modifyCarNumberTb = this.guide?.carNumber
        if(!this.modifyCarNumber.isNullOrEmpty()) {
            this.modifyCarNumberTb += "(" + this.modifyCarNumber + ")"
        }
    }
}
