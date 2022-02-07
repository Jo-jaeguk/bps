package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import com.mobilityk.core.dto.api.guidePriceConfig.TableDetailVM
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
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotNull

@Entity
@Table(
    name = "tb_guide_price_config",
    uniqueConstraints = [UniqueConstraint(columnNames = ["price_config_type" , "county_type" , "popular_type"])]
)
@NoArgsConstructor
@AllArgsConstructor
data class GuidePriceConfig(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "price_config_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    var priceConfigType: PriceConfigType? = null,

    @Column(name = "county_type")
    @Enumerated(EnumType.STRING)
    var countryType: CountryType? = null,

    @Column(name = "popular_type")
    @Enumerated(EnumType.STRING)
    var popularType: PopularType? = null,

    @Column(name = "value")
    @NotNull
    var value: String? = null,

) : Comparable<GuidePriceConfig> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuidePriceConfig): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun updateTableData(tableDetailVM: TableDetailVM, realPrice: String){
        this.popularType = tableDetailVM.popularType
        this.priceConfigType = tableDetailVM.priceConfigType
        this.countryType = tableDetailVM.countryType
        this.value = realPrice
    }
}


enum class PriceConfigType(var description: String) {
    RETAIL_CONST("소매 상수"), AVERAGE("평균"),
    T_100("100이하"),
    T_200("200"),
    T_300("300"),
    T_400("400"),
    T_500("500"),
    T_600("600"),
    T_700("700"),
    T_800("800"),
    T_900("900"),
    T_1000("1000"),
    T_1100("1100"),
    T_1200("1200"),
    T_1300("1300"),
    T_1400("1400"),
    T_1500("1500"),
    T_1600("1600"),
    T_1700("1700"),
    T_1800("1800"),
    T_1900("1900"),
    T_2000("2000"),
    T_2100("2100"),
    T_2200("2200"),
    T_2300("2300"),
    T_2400("2400"),
    T_2500("2500"),
    T_2600("2600"),
    T_2700("2700"),
    T_2800("2800"),
    T_2900("2900"),
    T_3000("3000"),
    T_3100("3100"),
    T_3200("3200"),
    T_3300("3300"),
    T_3400("3400"),
    T_3500("3500"),
    T_3600("3600"),
    T_3700("3700"),
    T_3800("3800"),
    T_3900("3900"),
    T_4000("4000"),
    T_MORE_4000("4000이상"),

    AVG_T_100("100이하 구간평균"),
    AVG_T_200("200 구간평균"),
    AVG_T_300("300 구간평균"),
    AVG_T_400("400 구간평균"),
    AVG_T_500("500 구간평균"),
    AVG_T_600("600 구간평균"),
    AVG_T_700("700 구간평균"),
    AVG_T_800("800 구간평균"),
    AVG_T_900("900 구간평균"),
    AVG_T_1000("1000 구간평균"),
    AVG_T_1100("1100 구간평균"),
    AVG_T_1200("1200 구간평균"),
    AVG_T_1300("1300 구간평균"),
    AVG_T_1400("1400 구간평균"),
    AVG_T_1500("1500 구간평균"),
    AVG_T_1600("1600 구간평균"),
    AVG_T_1700("1700 구간평균"),
    AVG_T_1800("1800 구간평균"),
    AVG_T_1900("1900 구간평균"),
    AVG_T_2000("2000 구간평균"),
    AVG_T_2100("2100 구간평균"),
    AVG_T_2200("2200 구간평균"),
    AVG_T_2300("2300 구간평균"),
    AVG_T_2400("2400 구간평균"),
    AVG_T_2500("2500 구간평균"),
    AVG_T_2600("2600 구간평균"),
    AVG_T_2700("2700 구간평균"),
    AVG_T_2800("2800 구간평균"),
    AVG_T_2900("2900 구간평균"),
    AVG_T_3000("3000 구간평균"),
    AVG_T_3100("3100 구간평균"),
    AVG_T_3200("3200 구간평균"),
    AVG_T_3300("3300 구간평균"),
    AVG_T_3400("3400 구간평균"),
    AVG_T_3500("3500 구간평균"),
    AVG_T_3600("3600 구간평균"),
    AVG_T_3700("3700 구간평균"),
    AVG_T_3800("3800 구간평균"),
    AVG_T_3900("3900 구간평균"),
    AVG_T_4000("4000 구간평균"),
    AVG_T_MORE_4000("4000이상  구간평균"),


    LESS_THAN_500("500이하"),
    RANGE_500_800("500~800"),
    RANGE_800_1100("800~1100"),
    RANGE_1100_1400("1100~1400"),
    RANGE_1400_1700("1400~1700"),
    RANGE_1700_2100("1700~2100"),
    RANGE_2100_2500("2100~2500"),
    RANGE_2500_3000("2500~3000"),
    RANGE_3000_4000("3000~4000"),
    RANGE_4000_5000("4000~5000"),
    GREATER_THAN_5000("5,000이상"),
    RANGE_500_1000("500~1,000"),
    RANGE_1000_1500("1,000~1,500"),
    RANGE_1500_2000("1,500~2,000"),
    RANGE_2000_2500("2,000~2,500"),

    /*
    RANGE_2500_3000("2,500~3,000"),
    RANGE_3000_4000("3,000~4,000"),
    RANGE_4000_5000("4,000~5,000"),
    GREATER_THAN_5000("5,000이상")
    */
}