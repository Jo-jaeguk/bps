package com.mobilityk.core.dto

import com.mobilityk.core.domain.Accident
import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.FuelType
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.domain.MotorType
import com.mobilityk.core.domain.TireWearType
import com.mobilityk.core.dto.api.guide.GuideCommentForAdminDTO
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Lob

data class GuideDTO(

    var id: Long? = null,

    var serial: String? = null,

    var member: MemberDTO? = null,

    var customerManager: MemberDTO? = null, // 고객담당자

    var price: Long? = null,

    var retailAvgPrice: Long? = null,

    var bidAvgPrice: Long? = null,

    var checkMinusPrice: Long? = null,

    var finalMinusPrice: Long? = null,

    var lossPrice: Long? = null,

    var finalBuyPrice: Long? = null,

    var finalBuyPriceSendYn: EnumYn? = null,

    var memberId: Long? = null,

    var memberEmail: String? = null,

    var memberName: String? = null,

    var memberPhone: String? = null,

    var memberGroup: String? = null,

    var memberBranch: String? = null,

    var memberManageBranch: String? = null,

    var adminId: Long? = null,

    var adminName: String? = null,

    var sendYn: EnumYn? = null, //가이드 전송 여부

    var inspectedYn: EnumYn? = null,

    var inspectTopImgUrl: String? = null,

    var inspectSideImgUrl: String? = null,

    var inspectBottomImgUrl: String? = null,

    var adminPhone: String? = null,

    var adminPosition: String? = null,

    var country: Country? = null,

    var carLocation: String? = null,

    var carColor: String? = null,

    var carManufacturer: String? = null,

    var carManufacturerId: Long? = null,

    var carNumber: String? = null,

    var carModel: String? = null,

    var carModelDetail: String? = null,

    var carModelId: Long? = null,

    var carClassName: String? = null,

    var carClassId: Long? = null,

    var guideIndex: Int? = null,

    var sendPrice: Long? = null, //가이드 전송 가격

    var sendPriceTemp: String? = null, //가이드 전송 가격

    var sellPrice: Long? = null, //판매가

    var carTrim: String? = null,

    var carTrimId: Long? = null,

    var carDisplacement: Int? = null,

    var carMadedYear: Int? = null,

    var carMadedMonth: Int? = null,

    var accident: Accident? = null,

    var accidentStr: String? = null,

    var fuelType: FuelType? = null,

    var motorType: MotorType? = null,

    var mileage: Int? = null,

    var realMileage: Int? = null,

    var option1: String? = null,

    var option2: String? = null,

    var option3: String? = null,

    var customerName: String? = null,

    var customerContact: String? = null,

    var guideStatus: GuideStatus? = null,

    var guideStatusStr: String? = null,

    var damageMinusPrice: Long? = null,

    var evaluatorMinusPrice: Long? = null,

    var evaluatorMinusPriceMil: Long? = null,

    var evaluatorMinusReason: String? = null,

    var priceList: List<GuidePriceDTO>? = null,

    var options: List<GuideOptionDTO>? = null,

    var damages: List<GuideDamageCheckDTO>? = null,

    var checkList: List<GuideCheckListDTO>? = null,

    var damageTotalPrice: Long? = null,

    var damageGiGyohwanCnt: Long? = null,

    var damageGiPangumCnt: Long? = null,

    var damageGiDosaekCnt: Long? = null,

    var damageNeedGyohwanCnt: Long? = null,

    var damageNeedPangumCnt: Long? = null,

    var damageNeedDosaekCnt: Long? = null,

    var damageOtherCnt: Long? = null,

    var damageGiGyohwanPrice: Long? = null,

    var damageGiPangumPrice: Long? = null,

    var damageGiDosaekPrice: Long? = null,

    var damageNeedGyohwanPrice: Long? = null,

    var damageNeedPangumPrice: Long? = null,

    var damageNeedDosaekPrice: Long? = null,

    var damageOtherPrice: Long? = null,

    //var commentList: List<GuideCommentDTO>? = null,

    var commentList: List<GuideCommentForAdminDTO>? = null,

    var additionalImages: List<GuideAddImgDTO>? = null,

    var damageImages: List<GuideAddImgDTO>? = null,

    var inspectCommentList: List<GuideCommentForAdminDTO>? = null,

    var guideNotificationList: List<GuideNotificationDTO>? = null,

    var guideHistory: List<GuideDTO>? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null,

    var tireWearType: TireWearType? = null,      // 타이어 마모 개수

    var tireWearReason: String? = null,

    var tireWearPrice: Long? = null,

    var newCarPrice: Long? = null,

    var carType: CarTypeEnum? = null,

    var guideEncarPrice: Long? = null,

    var guideKcarPrice: Long? = null,

    var guideKBPrice: Long? = null,

    var guideHowPrice: Long? = null,

    var guideCarAuctionBid: Long? = null,

    var guideKcarBid: Long? = null,

    var carDaeGum: Long? = null,    // 차량대금

    var sellPriceHistoryTb: Long? = null,    // 판매가

    var sellRevenue: Long? = null,    // 판매가

) {
    fun convertData() {
        this.guideStatusStr = this.guideStatus?.description
        this.accidentStr = this.accident?.description
        this.sendPrice = if(this.sendPrice == null) 0L else this.sendPrice
        if(this.sendPrice == 0L) {
            this.sendPriceTemp = "0"
        } else {
            this.sendPriceTemp = (this.sendPrice!! / 10000).toDouble().toString()
        }

        if(this.evaluatorMinusPrice != null) {
            this.evaluatorMinusPriceMil = (this.evaluatorMinusPrice?.toDouble()!! / 10000).toLong()
        }

    }
}