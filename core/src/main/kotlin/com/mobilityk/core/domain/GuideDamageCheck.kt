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
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide_damage_check"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuideDamageCheck(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_guide")
    @NotNull
    var guide: Guide? = null,

    @Column(name = "price")
    var price: Long? = null,

    @Column(name = "damage_type")
    @Enumerated(EnumType.STRING)
    var damageType: DamageType? = null,

    @Column(name = "damage_location")
    @Enumerated(EnumType.STRING)
    var damageLocation: DamageLocation? = null,

    @Column(name = "car_type")
    @Enumerated(EnumType.STRING)
    var carType: CarTypeEnum? = null,

) : Comparable<GuideDamageCheck> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuideDamageCheck): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun create(
        guide: Guide,
        carType: CarTypeEnum,
        damageLocation: DamageLocation,
        damageType: DamageType,
        price: Long
    ) {
        this.guide = guide
        this.carType = carType
        this.damageLocation = damageLocation
        this.damageType = damageType
        this.price = price
    }
}

