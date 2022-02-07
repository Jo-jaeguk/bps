package com.mobilityk.core.domain.buyandsell


import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.base.BaseEntity
import com.mobilityk.core.dto.buyandsell.SellDTO
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
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_sell")
@NoArgsConstructor
@AllArgsConstructor
data class Sell(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_buy_and_sell")
    @NotNull
    var buySell: BuyAndSell? = null,

    // 수입 여부
    @Column(name = "income_yn")
    @Enumerated(value = EnumType.STRING)
    var incomeYn: EnumYn? = null,

    // 필요서류 확인
    @Column(name = "need_document_yn")
    @Enumerated(value = EnumType.STRING)
    var needDocumentYn: EnumYn? = null,

    @Column(name = "sell_target")
    var sellTarget: String? = null,

    @Column(name = "hope_price")
    var hopePrice: Long? = null,

    @Column(name = "sell_price")
    var sellPrice: Long? = null,

    @Column(name = "other_commission")
    var otherCommission: Long? = null,

    @Column(name = "order_index")
    var orderIndex: Int? = null,

    @Column(name = "note")
    @Lob
    var note: String? = null,


) : Comparable<Sell> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Sell): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun create(buyAndSell: BuyAndSell, sellDTO: SellDTO) {
        this.buySell = buyAndSell
        this.hopePrice = if(sellDTO.hopePrice == null) 0L else sellDTO.hopePrice
        this.incomeYn = sellDTO.incomeYn
        this.needDocumentYn = sellDTO.needDocumentYn
        this.note = sellDTO.note
        this.sellPrice = if(sellDTO.sellPrice == null) 0L else sellDTO.sellPrice
        this.sellTarget = sellDTO.sellTarget
        this.otherCommission = if(sellDTO.otherCommission == null) 0L else sellDTO.otherCommission
        this.orderIndex = sellDTO.orderIndex
    }

    fun update(sellDTO: SellDTO) {
        this.hopePrice = if(sellDTO.hopePrice == null) 0L else sellDTO.hopePrice
        this.incomeYn = sellDTO.incomeYn
        this.needDocumentYn = sellDTO.needDocumentYn
        this.note = sellDTO.note
        this.sellPrice = if(sellDTO.sellPrice == null) 0L else sellDTO.sellPrice
        this.sellTarget = sellDTO.sellTarget
        this.otherCommission = if(sellDTO.otherCommission == null) 0L else sellDTO.otherCommission
        this.orderIndex = sellDTO.orderIndex
    }
}
