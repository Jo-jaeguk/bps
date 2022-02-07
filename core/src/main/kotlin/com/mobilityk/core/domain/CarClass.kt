package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
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
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_car_class")
@NoArgsConstructor
@AllArgsConstructor
data class CarClass(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_car_maodel")
    @NotNull
    var carModel: CarModel? = null,

    @Column(name = "name")
    @NotNull
    var name: String? = null,

    @Column(name = "car_displacement")
    var carDisplacement: Int? = null,

    @Column(name = "published")
    @NotNull
    var published: Boolean? = null,

    @Column(name = "popular_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    var popularType: PopularType? = null,

    @OneToMany(mappedBy = "carClass" , cascade = [CascadeType.ALL])
    var carTrimList: MutableList<CarTrim>? = mutableListOf(),

) : Comparable<CarClass> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: CarClass): Int {
        return if(this.id == other.id) 1 else -1
    }

}

enum class PopularType(var description: String) {
    POPULAR("인기"),
    UN_POPULAR("비인기"),
    ESSENTIAL("이건꼭")
}

data class CarClassSearchOption(
    var carModelId: Long? = null,
    var search: String? = null,
    var name: String? = null,
    var published: Boolean? = null,
)
