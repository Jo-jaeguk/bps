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
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_buy")
@NoArgsConstructor
@AllArgsConstructor
data class Buy(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_buy_and_sell")
    @NotNull
    var buySell: BuyAndSell? = null,

    @Column(name = "item_type")
    @Enumerated(value = EnumType.STRING)
    var itemType: ItemType? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "price")
    var price: Long? = null,

) : Comparable<Buy> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Buy): Int {
        return if(this.id == other.id) 1 else -1
    }
}
