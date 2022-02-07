package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.api.guide.GuideCreateVM
import com.mobilityk.core.dto.api.guide.GuideInspectSaveVM
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.util.CommonUtil
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDate
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
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide",
    indexes = [
        Index(columnList = "member_id")
    ]
)
@NoArgsConstructor
@AllArgsConstructor
data class Guide(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "serial", unique = true)
    @NotNull
    var serial: String? = null,

    @Column(name = "member_id")
    @NotNull
    var memberId: Long? = null,

    @Column(name = "admin_id")
    var adminId: Long? = null, //가이드 제출자

    @Column(name = "admin_name")
    var adminName: String? = null, //가이드 제출자

    @Column(name = "admin_phone")
    var adminPhone: String? = null, //가이드 제출자

    @Column(name = "admin_position")
    var adminPosition: String? = null, //가이드 제출자

    @Column(name = "send_yn")
    @Enumerated(EnumType.STRING)
    var sendYn: EnumYn? = null, //가이드 전송 여부

    @Column(name = "send_price")
    var sendPrice: Long? = null, //가이드 전송 가격

    @Column(name = "sell_price")
    var sellPrice: Long? = null, //가이드 전송 가격

    @Column(name = "price")
    var price: Long? = null,

    @Column(name = "retail_avg_price")
    var retailAvgPrice: Long? = null,

    @Column(name = "bid_avg_price")
    var bidAvgPrice: Long? = null,

    @Column(name = "check_minus_price")
    var checkMinusPrice: Long? = null,

    @Column(name = "damage_minus_price")
    var damageMinusPrice: Long? = null,

    @Column(name = "final_minus_price")
    var finalMinusPrice: Long? = null,

    @Column(name = "loss_price")
    var lossPrice: Long? = null,

    @Column(name = "final_buy_price")
    var finalBuyPrice: Long? = null,

    @Column(name = "final_buy_price_send_yn")
    @Enumerated(EnumType.STRING)
    var finalBuyPriceSendYn: EnumYn? = null,

    @Column(name = "country")
    @Enumerated(EnumType.STRING)
    var country: Country? = null,

    @Column(name = "car_location")
    var carLocation: String? = null,

    @Column(name = "car_color")
    var carColor: String? = null,

    @Column(name = "car_manufacturer")
    @NotNull
    var carManufacturer: String? = null,

    @Column(name = "car_manufacturer_id")
    var carManufacturerId: Long? = null,

    @Column(name = "car_number")
    var carNumber: String? = null,

    @Column(name = "car_model")
    @NotNull
    var carModel: String? = null,

    @Column(name = "car_model_detail")
    @NotNull
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

    @Column(name = "guide_index")
    var guideIndex: Int? = null,

    @Column(name = "mileage")
    var mileage: Int? = null,

    @Column(name = "real_mileage")
    var realMileage: Int? = null,

    // 기타사항으로 변경
    @Column(name = "option_1")
    var option1: String? = null,

    // 기타사항으로 변경
    @Column(name = "option_2")
    var option2: String? = null,

    // 기타사항으로 변경
    @Column(name = "option_3")
    var option3: String? = null,

    @Column(name = "customer_name")
    var customerName: String? = null,

    @Column(name = "customer_contact")
    var customerContact: String? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var guideStatus: GuideStatus? = null,

    @Column(name = "inspect_img_url")
    var inspectImgUrl: String? = null,

    @Column(name = "inspect_top_img_url")
    var inspectTopImgUrl: String? = null,

    @Column(name = "inspect_side_img_url")
    var inspectSideImgUrl: String? = null,

    @Column(name = "inspect_bottom_img_url")
    var inspectBottomImgUrl: String? = null,

    @Column(name = "inspected_yn")
    @Enumerated(EnumType.STRING)
    var inspectedYn: EnumYn? = null,

    @Column(name = "evaluator_minus_price")
    var evaluatorMinusPrice: Long? = null,

    @Column(name = "evaluator_minus_reason")
    @Lob
    var evaluatorMinusReason: String? = null,

    @Column(name = "tire_wear_type")
    @Enumerated(value = EnumType.STRING)
    var tireWearType: TireWearType? = null,      // 타이어 마모 개수

    @Column(name = "tire_wear_price")
    var tireWearPrice: Long? = null,

    @Column(name = "tire_wear_reason")
    @Lob
    var tireWearReason: String? = null,

    @Column(name = "car_type")
    @Enumerated(value = EnumType.STRING)
    var carType: CarTypeEnum? = null,

    @Column(name = "new_car_price")
    var newCarPrice: Long? = null,

    @OneToMany(mappedBy = "guide" , cascade = [CascadeType.ALL])
    var options: MutableList<GuideOption>? = mutableListOf(),

    @OneToMany(mappedBy = "guide" , cascade = [CascadeType.ALL])
    var additionalImages: MutableList<GuideAddImg>? = mutableListOf(),

    @OneToMany(mappedBy = "guide" , cascade = [CascadeType.ALL])
    var priceList: MutableList<GuidePrice>? = mutableListOf(),

    @OneToMany(mappedBy = "guide" , cascade = [CascadeType.ALL])
    var damages: MutableList<GuideDamageCheck>? = mutableListOf(),

    @OneToMany(mappedBy = "guide" , cascade = [CascadeType.ALL])
    var checkList: MutableList<GuideCheckList>? = mutableListOf(),

    @OneToMany(mappedBy = "guide" , cascade = [CascadeType.ALL])
    var commentList: MutableList<GuideComment>? = mutableListOf(),

) : Comparable<Guide> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Guide): Int {
        return if(this.id == other.id) 1 else -1
    }

    private fun dataUpdate(guideCreateVM: GuideCreateVM){
        this.accident = guideCreateVM.accident
        this.carDisplacement = guideCreateVM.carDisplacement
        this.carLocation = guideCreateVM.carLocation
        this.carMadedYear = guideCreateVM.carMadedYear
        this.carManufacturer = guideCreateVM.carManufacturer
        this.carModel = guideCreateVM.carModel
        this.carClassName = guideCreateVM.carClassName
        this.carNumber = guideCreateVM.carNumber
        this.carTrim = guideCreateVM.carTrim
        this.country = guideCreateVM.country
        this.customerContact = guideCreateVM.customerContact
        this.customerName = guideCreateVM.customerName
        this.fuelType = guideCreateVM.fuelType
        this.memberId = guideCreateVM.memberId
        this.mileage = guideCreateVM.mileage
        this.motorType = guideCreateVM.motorType

        this.option1 = guideCreateVM.option1
        this.option2 = guideCreateVM.option2
        this.option3 = guideCreateVM.option3
        this.guideStatus = GuideStatus.REQUEST
    }

    fun createGuide(guideCreateVM: GuideCreateVM) {
        fun getSerialNumber(): String {
            return "BC" + LocalDate.now().year
        }
        dataUpdate(guideCreateVM)
        this.serial = getSerialNumber()
    }

    fun updateGuide(guideCreateVM: GuideCreateVM) {
        dataUpdate(guideCreateVM)
    }

    fun updateGuideStatus(guideStatus: GuideStatus) {
        this.guideStatus = guideStatus
    }

    fun makeSerial(memberDTO: MemberDTO, branchNumber: String, guideIndex: Int) {
        id?.let {

            var strOne = if(memberDTO.roles?.contains(ROLE.MASTER)!! ||
                memberDTO.roles?.contains(ROLE.ADMIN)!!
            ) {
                "BC"
            } else {
                "CC"
            }
            var strTwo = branchNumber

            val now = LocalDateTime.now()
            var strThree = now.year.toString().substring(2) + CommonUtil.fillZero(now.monthValue , 2) + CommonUtil.fillZero(now.dayOfMonth, 2)
            var strFour = CommonUtil.fillZero(guideIndex, 3)

            /*
            val serialNumber = if(it >= 1000000) {
                (it % 1000000).toInt()
            } else {
                it.toInt()
            }
             */
            //this.serial = "BC" + LocalDate.now().year + CommonUtil.fillZero(serialNumber)
            this.serial = strOne + strTwo + strThree + strFour
            this.guideIndex = guideIndex
        }
    }

    fun saveInspect(inspectSaveVM: GuideInspectSaveVM) {
        this.mileage = inspectSaveVM.mileage    // 실주행거리를 주행거리로 업데이트 한다
        this.realMileage = inspectSaveVM.mileage
        this.carType = inspectSaveVM.carType

        this.tireWearType = inspectSaveVM.tireWearType
        this.tireWearPrice = inspectSaveVM.tireWearPrice
        this.tireWearReason = inspectSaveVM.tireWearReason

        this.checkMinusPrice = if(inspectSaveVM.checkListMinusPrice == null) 0L else inspectSaveVM.checkListMinusPrice
        var damageTotalPrice = 0L
        inspectSaveVM.minus?.damageList?.forEach { damageInfo ->
            damageTotalPrice += damageInfo.price!!
        }
        this.damageMinusPrice = damageTotalPrice
        this.evaluatorMinusPrice = if(inspectSaveVM.evaluatorMinusPrice == null) 0L else inspectSaveVM.evaluatorMinusPrice
        this.evaluatorMinusReason = inspectSaveVM.evaluatorMinusReason

       //        this.finalBuyPrice = this.sendPrice!! - (this.damageMinusPrice!! + this.evaluatorMinusPrice!! + this.checkMinusPrice!!)
        // 최종 매입 가격은 전송가격 - 평사가 감가금액으로 합니다.
        this.finalBuyPrice = this.sendPrice!! - (this.evaluatorMinusPrice!!)
        this.inspectedYn = EnumYn.Y
        this.finalBuyPriceSendYn = EnumYn.Y

        if(!inspectSaveVM.carNumber.isNullOrEmpty()){
            this.carNumber = inspectSaveVM.carNumber
        }
    }

    fun deleteInspect() {
        this.inspectedYn = EnumYn.N
        this.inspectTopImgUrl = ""
    }
}

enum class Accident (var description: String) {
    NONE("무사고"), SIMPLE_CHANGE("단순교환"), INSURANCE("보험사고"), SPECIAL("특수사고(전손/침수)")
}

enum class FuelType (var description: String) {
    GAS("가솔린"), DIESEL("디젤"), LPG("LPG"),
    GAS_LPG("가솔린(LPG겸용)"),
    GAS_CNG("가솔린(CNG겸용)"),
    GAS_HYBRID("가솔린 하이브리드"),
    ELECTRIC("전기"),
    HYDROGEN("수소")
}

enum class MotorType(var description: String) {
    MANUAL("수동"), AUTOMATIC("자동"), CVT("CVT"),
    DUAL("듀얼클러치")
}

enum class GuideStatus(var description: String) {
    REQUEST("가이드요청"),
    FINISH("완료"),
    BUY_REQUEST("매입요청"),
    BUY("매입"),
    AUCTION_SUCCESS("경매(낙찰)"),
    AUCTION_FAIL("경매(유찰)"),
    WAIT_FOR_MAX("최고가대기"),
    WAIT_FOR_SECOND("2등대기"),
    SELLING("판매중"),
    SELL("매각"),
    ING("조회용")
}

enum class TireWearType(var description: String) {
    EA_0("0개"),
    EA_1("1개"),
    EA_2("2개"),
    EA_3("3개"),
    EA_4("4개"),
    OTHER("기타"),
}

enum class EnumYn(var description: String) {
    Y("Y") , N("N")
}


data class GuideSearchOption(
    var memberId: Long? = null,
    var guideStatusList: List<GuideStatus>? = null,
    var guideStatus: GuideStatus? = null,
    var guideStatusIng: GuideStatus? = null,
    var search: String? = null,
    var sendYn: EnumYn? = null,
    var inspectedYn: EnumYn? = null,
    var startedAt: LocalDateTime? = null,
    var endedAt: LocalDateTime? = null,
    var carManufacturerName: String? = null,
    var carModelName: String? = null,
    var carModelDetailName: String? = null,
    var carTrim: String? = null,
    var carTrimEq: String? = null,
)

