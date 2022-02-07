package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import com.mobilityk.core.dto.api.carManufacturer.CarManufacturerVM
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.CascadeType
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
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_car_type")
@NoArgsConstructor
@AllArgsConstructor
data class CarType(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "car_type")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    var carType: CarTypeEnum? = null,

) : Comparable<CarType> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: CarType): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class CarTypeEnum(var description: String) {
    SMALL("소형"),
    MID("중형"),
    BIG("대형"),
    RV("RV"),
    FOREIGN("수입")
}
