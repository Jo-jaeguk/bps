package com.mobilityk.core.domain.buyandsell


import com.mobilityk.core.domain.base.BaseEntity
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "tb_buy_and_sell"
)
@NoArgsConstructor
@AllArgsConstructor
data class BuyAndSell(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "guide_id")
    var guideId: Long? = null,

    @Column(name = "buyer_id")
    var buyerId: Long? = null,

    @Column(name = "successed_at")
    var successedAt: LocalDateTime? = null,

    @Column(name = "deposited_at")
    var depositedAt: LocalDateTime? = null,

    @Column(name = "buy_type")
    var buyType: String? = null,

    @Column(name = "modify_car_number")
    var modifyCarNumber: String? = null,

    @Column(name = "new_car_dealer_location")
    var newCarDealerLocation: String? = null,

    @Column(name = "new_car_dealer_name")
    var newCarDealerName: String? = null,

    @Column(name = "seller")
    var seller: String? = null,

    @Column(name = "car_location")
    var carLocation: String? = null,

    @Column(name = "buy_date")
    var buyDate: LocalDateTime? = null,

    @Column(name = "sell_date")
    var sellDate: LocalDateTime? = null,

    // 매출원가 계
    @Column(name = "sell_total_price")
    var sellTotalPrice: Long? = null,

    // 판매예상가
    @Column(name = "sell_expect_price")
    var sellExpectPrice: Long? = null,

    // 판매가
    @Column(name = "sell_sell_price")
    var sellSellPrice: Long? = null,

    // 판매희망가
    @Column(name = "sell_hope_price")
    var sellHopePrice: Long? = null,

    // 기타 수수료 수입
    @Column(name = "sell_other_commission")
    var sellOtherCommission: Long? = null,

    // 판매가 차액
    @Column(name = "sell_diff_price")
    var sellDiffPrice: Long? = null,

    // 매입원가 계
    @Column(name = "buy_total_price")
    var buyTotalPrice: Long? = null,

    // 예상이익
    @Column(name = "expect_revevue")
    var expectRevenue: Long? = null,

    // 매출이익
    @Column(name = "sell_revevue")
    var sellRevenue: Long? = null,

    // 매출이익 차액
    @Column(name = "sell_revevue_diff")
    var sellRevenueDiff: Long? = null,

    // 차량대금
    @Column(name = "car_price")
    var carPrice: Long? = null,

    @Column(name = "bid_success_price")
    var bidSuccessPrice: Long? = null,

    @OneToMany(mappedBy = "buySell" , cascade = [CascadeType.ALL])
    var buyList: MutableList<Buy>? = mutableListOf(),

    @OneToMany(mappedBy = "buySell" , cascade = [CascadeType.ALL])
    var sells: MutableList<Sell>? = mutableListOf(),


) : Comparable<BuyAndSell> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: BuyAndSell): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun create(guideId: Long) {
        this.guideId = guideId
        this.sellTotalPrice = 0L
        this.sellSellPrice = 0L
        this.sellOtherCommission = 0L
        this.sellRevenue = 0L
        this.expectRevenue = 0L
        this.buyTotalPrice = 0L
        this.sellDiffPrice = 0L
        this.sellHopePrice = 0L
        this.sellExpectPrice = 0L
        this.sellRevenueDiff = 0L
        this.buyDate = LocalDateTime.now()
    }
}

data class BuyAndSellSearch(
    var search: String? = null,
    var carNumber: String? = null,
    var buyStartedAt: LocalDateTime? = null,
    var buyEndedAt: LocalDateTime? = null,
    var buySuccessStartedAt: LocalDateTime? = null,
    var buySuccessEndedAt: LocalDateTime? = null,
    var carModelName: String? = null,
    var dealerName: String? = null,
)
