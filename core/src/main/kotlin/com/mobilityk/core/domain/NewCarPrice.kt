package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tb_new_car_price"
)
@NoArgsConstructor
@AllArgsConstructor
data class NewCarPrice(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "car_manufacturer_name")
    var carManufacturerName: String? = null,

    @Column(name = "car_model_name")
    var carModelName: String? = null,

    @Column(name = "car_model_detail_name")
    var carModelDetailName: String? = null,

    @Column(name = "car_trim_name")
    var carTrimName: String? = null,

    @Column(name = "price")
    var price: Long? = null,

) : Comparable<NewCarPrice> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: NewCarPrice): Int {
        return if(this.id == other.id) 1 else -1
    }

}
