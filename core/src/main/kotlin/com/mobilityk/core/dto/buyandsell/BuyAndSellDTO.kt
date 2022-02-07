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

    // 낙찰가
    var bidSuccessPrice: Long? = null,

    // 매출원가 계
    var sellTotalPrice: Long? = null,

    // 판매예상가
    var sellExpectPrice: Long? = null,

    // 판매가
    var sellSellPrice: Long? = null,

    // 판매희망가
    var sellHopePrice: Long? = null,

    // 기타 수수료 수입
    var sellOtherCommission: Long? = null,

    // 판매가 차액
    var sellDiffPrice: Long? = null,

    // 매입원가 계
    var buyTotalPrice: Long? = null,

    // 매입원가 계 테이블 데이터
    var buyTotalPriceTb: Long? = null,

    // 예상이익
    var expectRevenue: Long? = null,

    // 매출이익
    var sellRevenue: Long? = null,

    // 매출이익 차액
    var sellRevenueDiff: Long? = null,

    // 테이블 매출원가계 합
    var sellTotalPriceTb: Long? = null,

    // 매입항목 total sum
    var totalBuyItemPrice: Long? = null,

    // 차량대금
    var carPrice: Long? = null,

    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {
    fun convertData() {
        this.stockDuration = if(this.depositedAt != null) {
//            재고기간을 sellDate 가 아닌 depositedAt 로 사용합니다.
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
