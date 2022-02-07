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
import javax.persistence.Lob
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide_notification"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuideNotification(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "body")
    @Lob
    var body: String? = null,

    @Column(name = "writer_id")
    var writerId: Long? = null,

    @Column(name = "car_manufacturer_id")
    var carManufacturerId: Long? = null,

    @Column(name = "car_manufacturer_name")
    var carManufacturerName: String? = null,

    @Column(name = "car_model_name")
    var carModelName: String? = null,

    @Column(name = "car_model_id")
    var carModelId: Long? = null,

    @Column(name = "car_model_detail_name")
    var carModelDetailName: String? = null,

    @Column(name = "car_class_name")
    var carClassName: String? = null,

    @Column(name = "car_class_id")
    var carClassId: Long? = null,

    @Column(name = "car_trim_name")
    var carTrimName: String? = null,

    @Column(name = "car_trim_id")
    var carTrimId: Long? = null,


) : Comparable<GuideNotification> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuideNotification): Int {
        return if(this.id == other.id) 1 else -1
    }
}

data class GuideNotificationSearch(
    var search: String? = null,
    var carManufacturerName: String? = null,
    var carModelName: String? = null,
    var carModelDetailName: String? = null,
)
