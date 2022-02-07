package com.mobilityk.core.domain.buyandsell


import com.mobilityk.core.domain.base.BaseEntity
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tb_buy_item"
)
@NoArgsConstructor
@AllArgsConstructor
data class BuyItem(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "item_type")
    @Enumerated(value = EnumType.STRING)
    var itemType: ItemType? = null,

) : Comparable<BuyItem> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: BuyItem): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class ItemType(var description: String) {
    BUY_BASE_COST("매입원가"),
    BUY_PROD_PRICE("상품화비용"),
    BUY_BID_PRICE("경매비용"),
    BUY_RETAIL_PRICE("소매비용"),
}
