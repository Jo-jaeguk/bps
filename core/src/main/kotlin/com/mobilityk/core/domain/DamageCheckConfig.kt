package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import com.mobilityk.core.dto.DamageCheckConfigDTO
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

@Entity
@Table(name = "tb_damage_check_config"
)
@NoArgsConstructor
@AllArgsConstructor
data class DamageCheckConfig(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "number")
    var number: Int? = null,

    @Column(name = "car_type")
    @Enumerated(EnumType.STRING)
    var carType: CarTypeEnum? = null,

    @Column(name = "damage_location")
    @Enumerated(EnumType.STRING)
    var damageLocation: DamageLocation? = null,

    @Column(name = "damage_location_str")
    var damageLocationStr: String? = null,

    @Column(name = "gi_gyohwan_price")
    var giGyoHwanPrice: Long? = null,

    @Column(name = "gi_pangum_price")
    var giPangumPrice: Long? = null,

    @Column(name = "gi_dosaek_price")
    var giDosaekPrice: Long? = null,

    @Column(name = "need_gyohwan_price")
    var needGyoHwanPrice: Long? = null,

    @Column(name = "need_pangum_price")
    var needPangumPrice: Long? = null,

    @Column(name = "need_dosaek_price")
    var needDosaekPrice: Long? = null,

    @Column(name = "other_price")
    var otherPrice: Long? = null,


) : Comparable<DamageCheckConfig> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: DamageCheckConfig): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun update(damageCheckConfigDTO: DamageCheckConfigDTO) {
        this.needPangumPrice = damageCheckConfigDTO.needPangumPrice
        this.needGyoHwanPrice = damageCheckConfigDTO.needGyoHwanPrice
        this.needDosaekPrice = damageCheckConfigDTO.needDosaekPrice

        this.giPangumPrice = damageCheckConfigDTO.giPangumPrice
        this.giDosaekPrice = damageCheckConfigDTO.giDosaekPrice
        this.giGyoHwanPrice = damageCheckConfigDTO.giGyoHwanPrice

        this.otherPrice = damageCheckConfigDTO.otherPrice

        this.carType = damageCheckConfigDTO.carType
        this.damageLocation = damageCheckConfigDTO.damageLocation
        this.damageLocationStr = damageCheckConfigDTO.damageLocation?.description
    }
}

data class DamageCheckSearchOption(
    var search: String? = null,
    var carType: CarTypeEnum? = null,
)

enum class DamageType (var description: String) {
    GI_GYOHWAN("기교환"),
    GI_PANGUM("기판금"),
    GI_DOSAEK("기도색"),
    NEED_GYOHWAN("교환요망"),
    NEED_PANGUM("판금요망"),
    NEED_DOSAEK("도색요망"),
    OTHER("기타"),
}

enum class DamageLocation(var description: String) {
    AP_BUMPER("앞범퍼"),
    FRONT_RADIATOR_SUPPORT("프론트 라디에이터 서포트"),
    JUNJO_DEUNG("전조등(좌)"),
    ANGYE_DEUNG("안개등(좌)"),
    AP_WHEEL_LEFT("앞휠(좌)"),
    AP_TIRE_LEFT("앞타이어(좌)"),
    FRONT_FENDER("프론트 휀더(좌)"),
    BONNET("보닛"),
    UN_SIDE_SIL("(운)사이드실"),
    FRONT_DOOR_LEFT("프론드도어(좌)"),
    SIDE_MIRROR_LEFT("사이드미러(좌)"),
    AP_MIRROR("앞유리"),
    REAR_DOOR_LEFT("리어도어(좌)"),
    SUN_ROOF("선루프"),
    BACK_WHEEL_LEFT("뒤휠(좌)"),
    BACK_TIRE_LEFT("뒤타이어(좌)"),
    BACK_MIRROR("뒷유리"),
    TRUNK("트렁크"),
    REAR_COMBEE_LAMP_LEFT("리어컴비램프(좌)"),
    REAR_COMBEE_LAMP_RIGHT("리어컴비램프(우)"),
    BACK_BUMPER("뒤범퍼"),
    HEAD_LIGHT_RIGHT("전조등(우)"),
    FORG_LIGHT_RIGHT("안개등(우)"),
    FRONT_FENDER_RIGHT("프론트 휀더(우)"),
    AP_WHEEL_RIGHT("앞휠(우)"),
    AP_TIRE_RIGHT("앞타이어(우)"),
    SIDE_MIRROR_RIGHT("사이드미러(우)"),
    FRONT_DOOR_RIGHT("프론트도어(우)"),
    JO_SIDE_SIL("(조)사이드실"),
    REAR_DOOR_RIGHT("리어도어(우)"),
    BACK_WHEEL_RIGHT("뒤휠(우)"),
    BACK_TIRE_RIGHT("뒤타이어(우)"),
    UN_A_FILLER("(운)A필러"),
    UN_B_FILLER("(운)B필러"),
    UN_C_FILLER("(운)C필러"),
    ROOF("루프"),
    REAR_FENDER_LEFT("리어휀더(좌)"),
    REAR_FENDER_RIGHT("리어휀더(우)"),
    JO_A_FILLER("(조)A필러"),
    JO_B_FILLER("(조)B필러"),
    JO_C_FILLER("(조)C필러"),
    AP_PANNEL("앞패널"),
    AP_CROSS_MEMBER("앞크로스맴버"),
    AP_INSIDE_PANNEL_LEFT("앞인사이드패널(좌)"),
    AP_WHEEL_HOUSE_LEFT("앞휠하우스(좌)"),
    AP_SIDE_MEMBER_LEFT("앞사이드맴버(좌)"),
    AP_SIDE_MEMBER_RIGHT("앞사이드맴버(우)"),
    AP_SIDE_PANNEL_RIGHT("앞인사이드패널(우)"),
    AP_WHEEL_HOUSE_RIGHT("앞휠하우스(우)"),
    BACK_CROSS_MEMBER("뒷크로스맴버"),
    DASHBOARD("대쉬보드(카울패널)"),
    FLOOR_PANNEL("플로어패널"),
    REAR_PACKAGE_TRAY("리어패키지트레이"),
    BACK_WHEEL_HOUSE_LEFT("뒤휠하우스(좌)"),
    BACK_SIDE_MEMBER_LEFT("뒤사이드맴버(좌)"),
    TRUNK_FLOOR_PANNEL("트렁크플로어패널"),
    BACK_SIDE_MEMBER_RIGHT("뒤사이드맴버(우)"),
    BACK_WHEEL_HOUSE_RIGHT("뒤휠하우스(우)"),
    BACK_PANNEL("뒤패널"),
    JUK_JAE_HAM_BADAK("적재함바닥"),
    STEP_LEFT("스텝(좌)"),
    STEP_RIGHT("스텝(우)"),
    JUK_JAE_HAM_LEFT("적재함(좌) [탑차]"),
    JUK_JAE_HAM_RIGHT("적재함(우) [탑차]"),
    JUK_JAE_HAM_DOOR_LEFT("적재함도어(좌)"),
    JUK_JAE_HAM_DOOR_RIGHT("적재함도어(우)"),
    JUK_JAE_HAM_DOOR_BACK("적재함도어(후)"),
}

