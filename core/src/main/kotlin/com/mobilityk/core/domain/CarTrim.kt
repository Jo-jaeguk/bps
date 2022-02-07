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
@Table(name = "tb_car_trim")
@NoArgsConstructor
@AllArgsConstructor
data class CarTrim(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_car_class")
    @NotNull
    var carClass: CarClass? = null,

    @Column(name = "name")
    @NotNull
    var name: String? = null,

    @Column(name = "published")
    @NotNull
    var published: Boolean? = null,

) : Comparable<CarTrim> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: CarTrim): Int {
        return if(this.id == other.id) 1 else -1
    }
}

data class CarTrimSearchOption (
    var search: String? = null,
    var name: String? = null,
    var carClassId: Long? = null,
    var published: Boolean? = null,
)

