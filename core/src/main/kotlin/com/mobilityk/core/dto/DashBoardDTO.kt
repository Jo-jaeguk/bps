package com.mobilityk.core.dto

data class DashBoardDTO(
    var todayGuideRequestCnt: Long? = null,
    var todayGuideFinishCnt: Long? = null,
    val todayGuideOngoingCnt: Long? = null,
    var todayGuideBuyCnt: Long? = null,
    var todayGuideBuyMoney: Long? = null,
    var todayGuideSellingCnt: Long? = null,
    var todayGuideSellCnt: Long? = null,
    var todayGuideSellMoney: Long? = null,
    var totalSellMoney: Long? = null,
    var weekly: List<DashBoardGuide>? = null
)

data class DashBoardGuide(
    val index: Int? = null,
    val requestCnt: Long? = null,
    val finishCnt: Long? = null,
    val buyCnt: Long? = null,
    val buyMoney: Long? = null,
    val sellingCnt: Long? = null,
    val sellCnt: Long? = null,
    val sellMoney: Long? = null,
)