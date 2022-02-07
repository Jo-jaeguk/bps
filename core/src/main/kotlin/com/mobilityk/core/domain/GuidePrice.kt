package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide_price"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuidePrice(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_guide")
    @NotNull
    var guide: Guide? = null,

    @Column(name = "market_name")
    var marketName: String? = null,

    @Column(name = "market_type")
    @Enumerated(EnumType.STRING)
    var marketType: MarketType? = null,

    @Column(name = "market_name_code")
    @Enumerated(EnumType.STRING)
    var marketNameCode: MarketName? = null,

    @Column(name = "popular_type")
    @Enumerated(EnumType.STRING)
    var popularType: PopularType? = null,

    @Column(name = "price")
    @Min(0)
    var price: Long? = null,


) : Comparable<GuidePrice> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuidePrice): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class MarketType(var description: String) {
    RETAIL("소매가"), BIDING("경매가")
}

enum class MarketName(var description: String) {
    RETAIL_SK_ENCAR("SK엔카"),
    RETAIL_KCAR("KCar"),
    RETAIL_KB("KB차차차"),
    RETAIL_HOW("하우머치"),
    BID_KCAR("KCar 경매장"),
    BID_CAR_AUCTION("Car Auction 경매장"),
}



