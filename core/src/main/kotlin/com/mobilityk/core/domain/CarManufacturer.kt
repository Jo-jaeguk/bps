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
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_car_manufacturer",
    indexes = [
        Index(columnList = "country_type")
    ]
)
@NoArgsConstructor
@AllArgsConstructor
data class CarManufacturer(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "name", unique = true)
    var name: String? = null,

    @Column(name = "name_kr")
    var nameKr: String? = null,

    @Column(name = "name_eng")
    var nameEng: String? = null,

    @Column(name = "imgPath")
    var imgPath: String? = null,

    @Column(name = "published")
    @NotNull
    var published: Boolean? = null,

    @Column(name = "country_type")
    @Enumerated(EnumType.STRING)
    var countryType: CountryType? = null,

    @Column(name = "country")
    @Enumerated(EnumType.STRING)
    var country: Country? = null,

    @OneToMany(mappedBy = "carManufacturer" , cascade = [CascadeType.ALL])
    var carModelList: MutableList<CarModel>? = mutableListOf(),

) : Comparable<CarManufacturer> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: CarManufacturer): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun create(carManufacturerVM: CarManufacturerVM) {
        updateData(carManufacturerVM)
    }

    fun update(carManufacturerVM: CarManufacturerVM) {
        updateData(carManufacturerVM)
    }

    private fun updateData(carManufacturerVM: CarManufacturerVM){
        this.country = carManufacturerVM.country
        this.countryType = carManufacturerVM.countryType
        this.name = carManufacturerVM.name
        this.nameEng = carManufacturerVM.nameEng
        this.published = carManufacturerVM.published
    }

}

enum class CarManufacturerName(var description: String) {
    AUDI("?????????"), BENTLEY("?????????"), BENZ("??????"),
    BENZ_SMART("?????????"), BMW("BMW"), BMW_MINI("??????"),
    CADILLAC("?????????"), CHEVROLET("?????????"), CHRYSLER("???????????????"),
    CITROEN("????????????"), FERRARI("?????????"), FIAT("?????????"), FORD("??????"),
    GENESIS("????????????"), HONDA("??????"), HYUNDAI("??????"),
    INFINITY("????????????"), JAGUAR("?????????"), JEEP("??????"),
    KIA("??????"), LANDROVER("????????????"), LEXUS("?????????"),
    LINCOLN("??????"), MITSUBISHI("????????????"),MASERATI("????????????"), NISSAN("??????"),
    PEUGEOT("??????"), PORSCHE("?????????"),RENAULT("????????????"), ROLLSROYS("???????????????"),
    SSANGYONG("??????"), TESLA("?????????"), TOYOTA("?????????"),
    VOLKSWAGEN("????????????"), VOLVO("??????")
}

enum class Country {
    KOREA, GERMANY, USA, CHINA, JAPAN, FRANCE
}

enum class CountryType(var description: String) {
    DOMESTIC("??????"), INTERNATIONAL("??????")
}

data class CarManufacturerSearchOption(
    var countryType: CountryType? = null,
    var country: Country? = null,
    var published: Boolean? = null,
    var name: String? = null
)