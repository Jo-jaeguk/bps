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
@Table(name = "tb_config"
)
@NoArgsConstructor
@AllArgsConstructor
data class Config(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "config_type")
    @Enumerated(value = EnumType.STRING)
    var configType: ConfigType? = null,

    @Column(name = "value")
    var value: String? = null,

) : Comparable<Config> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Config): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class ConfigType(var description: String) {
    CHECK_LIST_BASE_PRICE("체크리스트 기본금액"),
    DAMAGE_BASE_SMALL_PRICE("데미지 소형 기준가"),
    DAMAGE_BASE_MID_PRICE("데미지 중형 기준가"),
    DAMAGE_BASE_BIG_PRICE("데미지 대형 기준가"),
    DAMAGE_BASE_RV_PRICE("데미지 대형 기준가"),
    DAMAGE_BASE_FOREIGN_PRICE("데미지 수입차 기준가"),

    DAMAGE_BASE_SMALL_RATIO("데미지 소형 비율"),
    DAMAGE_BASE_MID_RATIO("데미지 중형 비율"),
    DAMAGE_BASE_BIG_RATIO("데미지 대형 비율"),
    DAMAGE_BASE_RV_RATIO("데미지 RV 비율"),
    DAMAGE_BASE_FOREIGN_RATIO("데미지 수입차 비율"),

}