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
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide_temp"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuideTemp(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "member_id")
    @NotNull
    var memberId: Long? = null,

    @Column(name = "country")
    @Enumerated(EnumType.STRING)
    var country: Country? = null,

    @Column(name = "car_location")
    var carLocation: String? = null,

    @Column(name = "car_color")
    var carColor: String? = null,

    @Column(name = "car_manufacturer")
    var carManufacturer: String? = null,

    @Column(name = "car_manufacturer_id")
    var carManufacturerId: Long? = null,

    @Column(name = "car_number")
    var carNumber: String? = null,

    @Column(name = "car_model")
    var carModel: String? = null,

    @Column(name = "car_model_detail")
    var carModelDetail: String? = null,

    @Column(name = "car_model_id")
    var carModelId: Long? = null,

    @Column(name = "car_class_name")
    var carClassName: String? = null,

    @Column(name = "car_class_id")
    var carClassId: Long? = null,

    @Column(name = "car_trim")
    var carTrim: String? = null,

    @Column(name = "car_trim_id")
    var carTrimId: Long? = null,

    @Column(name = "car_displacement")
    var carDisplacement: Int? = null,

    @Column(name = "car_maded_year")
    var carMadedYear: Int? = null,

    @Column(name = "car_maded_month")
    var carMadedMonth: Int? = null,

    @Column(name = "accident")
    @Enumerated(EnumType.STRING)
    var accident: Accident? = null,

    @Column(name = "fuel_type")
    @Enumerated(EnumType.STRING)
    var fuelType: FuelType? = null,

    @Column(name = "motor_type")
    @Enumerated(EnumType.STRING)
    var motorType: MotorType? = null,

    @Column(name = "mileage")
    var mileage: Int? = null,

    @Column(name = "option_1")
    var option1: String? = null,

    @Column(name = "option_2")
    var option2: String? = null,

    @Column(name = "option_3")
    var option3: String? = null,

    @Column(name = "customer_name")
    var customerName: String? = null,

    @Column(name = "customer_contact")
    var customerContact: String? = null,

    @Column(name = "front_img_url")
    var frontImgUrl: String? = null,

    @Column(name = "back_img_url")
    var backImgUrl: String? = null,

    @Column(name = "left_img_url")
    var leftImgUrl: String? = null,

    @Column(name = "right_img_url")
    var rightImgUrl: String? = null,

    @Column(name = "gauge_img_url")
    var gaugeImgUrl: String? = null,



) : Comparable<GuideTemp> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuideTemp): Int {
        return if(this.id == other.id) 1 else -1
    }

}

