package com.mobilityk.core.dto

data class MemberDetailDTO(

    var member: MemberDTO? = null,

    var totalGuideCnt: Long? = null,

    var finishGuideCnt: Long? = null,

    var ingGuideCnt: Long? = null,

    var buyGuideCnt: Long? = null,

    var buyGuideMoney: Long? = null,

    var sellingGuideCnt: Long? = null,

    var sellingGuideMoney: Long? = null,

    var sellGuideCnt: Long? = null,

    var sellGuideMoney: Long? = null

)