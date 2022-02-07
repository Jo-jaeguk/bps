package com.mobilityk.core.dto

import java.time.LocalDateTime

data class GuideNotificationDTO(

    var id: Long? = null,

    var writer: MemberDTO? = null,

    var title: String? = null,

    var body: String? = null,

    var writerId: Long? = null,

    var carManufacturerName: String? = null,

    var carManufacturerId: Long? = null,

    var carModelName: String? = null,

    var carModelId: Long? = null,

    var carModelDetailName: String? = null,

    var carClassName: String? = null,

    var carClassId: Long? = null,

    var carTrimName: String? = null,

    var carTrimId: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null,

    var createdAtStr: String? = null,
) {
    fun convertData() {
        this.createdAtStr = this.createdAt?.year.toString() + "-" + this.createdAt?.monthValue + "-" + this.createdAt?.dayOfMonth
    }
}