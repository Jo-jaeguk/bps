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
@Table(name = "tb_car_model")
@NoArgsConstructor
@AllArgsConstructor
data class CarModel(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_car_manufacturer")
    @NotNull
    var carManufacturer: CarManufacturer? = null,

    @Column(name = "name")
    @NotNull
    var name: String? = null,

    @Column(name = "name_detail")
    @NotNull
    var nameDetail: String? = null,

    @Column(name = "published")
    @NotNull
    var published: Boolean? = null,

    @OneToMany(mappedBy = "carModel" , cascade = [CascadeType.ALL])
    var carClassList: MutableList<CarClass>? = mutableListOf(),

) : Comparable<CarModel> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: CarModel): Int {
        return if(this.id == other.id) 1 else -1
    }

}

data class CarModelSearchOption(
    var search: String? = null,
    var startedAt: LocalDateTime? = null,
    var endedAt: LocalDateTime? = null,
    var carManufacturerId: Long? = null,
    var carManufacturer: CarManufacturer? = null,
    var carModelName: String? = null,
    var carModelNameDetail: String? = null,
    var published: Boolean? = null,
    var carModelId: Long? = null,
)