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
    AUDI("아우디"), BENTLEY("벤틀리"), BENZ("벤츠"),
    BENZ_SMART("스마트"), BMW("BMW"), BMW_MINI("미니"),
    CADILLAC("캐딜락"), CHEVROLET("쉐보레"), CHRYSLER("크라이슬러"),
    CITROEN("시트로엥"), FERRARI("페라리"), FIAT("피아트"), FORD("포드"),
    GENESIS("제네시스"), HONDA("혼다"), HYUNDAI("현대"),
    INFINITY("인피니티"), JAGUAR("재규어"), JEEP("지프"),
    KIA("기아"), LANDROVER("랜드로버"), LEXUS("렉서스"),
    LINCOLN("링컨"), MITSUBISHI("미쯔비시"),MASERATI("마세라티"), NISSAN("닛산"),
    PEUGEOT("푸조"), PORSCHE("포르쉐"),RENAULT("르노삼성"), ROLLSROYS("롤스로이스"),
    SSANGYONG("쌍용"), TESLA("테슬라"), TOYOTA("도요타"),
    VOLKSWAGEN("폭스바겐"), VOLVO("볼보")
}

enum class Country {
    KOREA, GERMANY, USA, CHINA, JAPAN, FRANCE
}

enum class CountryType(var description: String) {
    DOMESTIC("국산"), INTERNATIONAL("수입")
}

data class CarManufacturerSearchOption(
    var countryType: CountryType? = null,
    var country: Country? = null,
    var published: Boolean? = null,
    var name: String? = null
)