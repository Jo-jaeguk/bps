package com.mobilityk.core.service

import com.mobilityk.core.domain.AccountType
import com.mobilityk.core.dto.api.nice.NiceUserInfo
import com.mobilityk.core.exception.CommException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSession

@Service
data class NiceAuthService(
    @Value("\${nice.site-password}")
    private val sitePassword: String,
    @Value("\${nice.site-code}")
    private val siteCode: String,
    @Value("\${nice.join.success-url}")
    private val joinSuccessUrl: String,
    @Value("\${nice.join.fail-url}")
    private val joinFailUrl: String,
    @Value("\${nice.search.id.success-url}")
    private val idSearchSuccessUrl: String,
    @Value("\${nice.search.id.fail-url}")
    private val idSearchFailUrl: String,
    @Value("\${nice.search.password.success-url}")
    private val pwSearchSuccessUrl: String,
    @Value("\${nice.search.password.fail-url}")
    private val pwSearchFailUrl: String,
    @Value("\${nice.change-phone.success-url}")
    private val changePhoneSuccessUrl: String,
    @Value("\${nice.change-phone.fail-url}")
    private val changePhoneFailUrl: String,
    @Value("\${nice.change-password.success-url}")
    private val changePasswordSuccessUrl: String,
    @Value("\${nice.change-password.fail-url}")
    private val changePasswordFailUrl: String

) {

    private val logger = KotlinLogging.logger {}

    fun getJoinEncData(
        httpSession: HttpSession,
        callBackUri: String?,
        accountType: AccountType?,
        id: Long?
    ): String {
        val niceCheck = NiceID.Check.CPClient()
        val sRequestNumber = niceCheck.getRequestNO("REQ0000000001")
        httpSession.setAttribute("REQ_SEQ" , sRequestNumber)

        val sAuthType = "M"
        val popgubun = "N"
        val customize = "Mobile"
        val sGender = ""

        val successUrlIn = joinSuccessUrl + "?accountType="+ accountType?.name + "&id=" + id + "&callBackUri="+callBackUri
        val failUrlIn = joinFailUrl + "?accountType="+ accountType?.name + "&id=" + id + "&callBackUri="+callBackUri

        return getEncData(sRequestNumber, sAuthType, popgubun, customize, sGender, successUrlIn, failUrlIn, httpSession)
    }


    fun getSearchEncData(
        httpSession: HttpSession,
        searchType: String
    ): String {
        val niceCheck = NiceID.Check.CPClient()
        val sRequestNumber = niceCheck.getRequestNO("REQ0000000001")
        httpSession.setAttribute("REQ_SEQ" , sRequestNumber)
        val sAuthType = "M"
        val popgubun = "N"
        val customize = "Mobile"
        val sGender = ""
        val successUrlIn = if(searchType == "ID") {
            idSearchSuccessUrl
        } else {
            logger.info { "pwSearchSuccessUrl $pwSearchSuccessUrl" }
            pwSearchSuccessUrl
        }
        val failUrlIn = if(searchType == "ID") {
            idSearchFailUrl
        } else {
            logger.info { "pwSearchFailUrl $pwSearchFailUrl" }
            pwSearchFailUrl
        }
        return getEncData(sRequestNumber, sAuthType, popgubun, customize, sGender, successUrlIn, failUrlIn, httpSession)
    }

    fun getChangePhoneEncData(
        httpSession: HttpSession,
        memberId: Long
    ): String {
        val niceCheck = NiceID.Check.CPClient()
        val sRequestNumber = niceCheck.getRequestNO("REQ0000000001")
        httpSession.setAttribute("REQ_SEQ" , sRequestNumber)
        val sAuthType = "M"
        val popgubun = "N"
        val customize = "Mobile"
        val sGender = ""

        val successUrl = "$changePhoneSuccessUrl?memberId=$memberId"
        val failUrl = "$changePhoneFailUrl?memberId=$memberId"

        logger.info { "change phone success url [$successUrl]" }
        logger.info { "change phone fail url [$failUrl]" }
        
        return getEncData(sRequestNumber, sAuthType, popgubun, customize, sGender, successUrl, failUrl, httpSession)
    }

    fun getChangePasswordEncData(
        httpSession: HttpSession,
        memberId: Long
    ): String {
        val niceCheck = NiceID.Check.CPClient()
        val sRequestNumber = niceCheck.getRequestNO("REQ0000000001")
        httpSession.setAttribute("REQ_SEQ" , sRequestNumber)
        val sAuthType = "M"
        val popgubun = "N"
        val customize = "Mobile"
        val sGender = ""

        val successUrl = "$changePasswordSuccessUrl?memberId=$memberId"
        val failUrl = "$changePasswordFailUrl?memberId=$memberId"

        logger.info { "change password success url [$successUrl]" }
        logger.info { "change password fail url [$failUrl]" }

        return getEncData(sRequestNumber, sAuthType, popgubun, customize, sGender, successUrl, failUrl, httpSession)
    }




    fun getEncData(
        sRequestNumber: String,
        sAuthType: String,
        popgubun: String,
        customize: String,
        sGender: String,
        successUrl: String,
        failUrl: String,
        httpSession: HttpSession
    ): String {
        val niceCheck = NiceID.Check.CPClient()
        val requestNumber = niceCheck.getRequestNO("REQ0000000001")
        httpSession.setAttribute("REQ_SEQ" , requestNumber)

        val sPlainData = StringBuilder()
        sPlainData.append("7:REQ_SEQ").append(requestNumber.toByteArray().size).append(":").append(requestNumber)
            .append("8:SITECODE").append(siteCode.toByteArray().size).append(":").append(siteCode)
            .append("9:AUTH_TYPE").append(sAuthType.toByteArray().size).append(":").append(sAuthType)
            .append("7:RTN_URL").append(successUrl.toByteArray().size).append(":").append(successUrl)
            .append("7:ERR_URL").append(failUrl.toByteArray().size).append(":").append(failUrl)
            .append("11:POPUP_GUBUN").append(popgubun.toByteArray().size).append(":").append(popgubun)
            .append("9:CUSTOMIZE").append(customize.toByteArray().size).append(":").append(customize)
            .append("6:GENDER").append(sGender.toByteArray().size).append(":").append(sGender)

        var sEncData: String
        when(val result = niceCheck.fnEncode(siteCode, sitePassword, sPlainData.toString())) {
            0 -> sEncData = niceCheck.cipherData
            -1 -> throw CommException("암호화 시스템 오류")
            -2 -> throw CommException("암호화 처리 오류")
            -3 -> throw CommException("암호화 데이터 오류")
            -9 -> throw CommException("입력 데이터 오류")
            else -> throw CommException("알수 없는 오류 $result")
        }
        return sEncData
    }

    fun getAuthInfo(
        httpSession: HttpSession,
        encodeData: String
    ): NiceUserInfo {
        val niceUserInfo = NiceUserInfo()
        val niceCheck = NiceID.Check.CPClient()
        val result = niceCheck.fnDecode(siteCode, sitePassword, encodeData)

        var plainData: String
        //var cipherTime = ""
        var sRequestNumber = "";			// 요청 번호
        //var sResponseNumber = "";		// 인증 고유번호
        var sAuthType = "";				// 인증 수단
        var sName = "";					// 성명
        var sDupInfo = "";				// 중복가입 확인값 (DI_64 byte)
        var sConnInfo = "";				// 연계정보 확인값 (CI_88 byte)
        var sBirthDate = "";				// 생년월일(YYYYMMDD)
        var sGender = "";				// 성별
        var sNationalInfo = "";			// 내/외국인정보 (개발가이드 참조)
        var sMobileNo = "";				// 휴대폰번호
        var sMobileCo = "";				// 통신사
        var sMessage = "";
        when(result) {
            0 -> {
                plainData = niceCheck.plainData
                //cipherTime = niceCheck.cipherDateTime
                var mapResult = niceCheck.fnParse(plainData)
                sRequestNumber = mapResult.get("REQ_SEQ").toString()
                //sResponseNumber = mapResult.get("RES_SEQ").toString()
                sAuthType = mapResult.get("AUTH_TYPE").toString()
                sName = mapResult.get("NAME").toString()
                //sName			= mapResult.get("UTF8_NAME"); //charset utf8 사용시 주석 해제 후 사용
                sBirthDate = mapResult.get("BIRTHDATE").toString()
                sGender = mapResult.get("GENDER").toString()
                sNationalInfo = mapResult.get("NATIONALINFO").toString()
                sDupInfo = mapResult.get("DI").toString()
                sConnInfo = mapResult.get("CI").toString()
                sMobileNo = mapResult.get("MOBILE_NO").toString()
                sMobileCo = mapResult.get("MOBILE_CO").toString()
                var sessionRequestNumber = httpSession.getAttribute("REQ_SEQ")
                if(sRequestNumber != sessionRequestNumber)
                {
                    sMessage = "세션값 불일치 오류입니다.";
                    sAuthType = "";
                }
                niceUserInfo.name = sName
                niceUserInfo.gender = sGender
                niceUserInfo.phone = sMobileNo
                niceUserInfo.phoneCompany = sMobileCo
                niceUserInfo.birthDate = sBirthDate
            }
            -1 -> { sMessage = "복호화 시스템 오류입니다." }
            -4 -> { sMessage = "복호화 처리 오류입니다." }
            -5 -> { sMessage = "복호화 해쉬 오류입니다." }
            -6 -> { sMessage = "복호화 데이터 오류입니다." }
            -9 -> { sMessage = "입력 데이터 오류입니다." }
            -12 -> { sMessage = "사이트 패스워드 오류입니다." }
            else -> { sMessage = "알수 없는 에러 입니다. iReturn : $result" }
        }
        niceUserInfo.message = sMessage
        niceUserInfo.result = result
        logger.info { "result $result sMessage $sMessage" }
        logger.info { "sRequestNumber $sRequestNumber" }
        logger.info { "sAuthType $sAuthType" }
        logger.info { "sName $sName" }
        logger.info { "sBirthDate $sBirthDate" }
        logger.info { "sGender $sGender" }
        logger.info { "sNationalInfo $sNationalInfo" }
        logger.info { "sDupInfo $sDupInfo" }
        logger.info { "sConnInfo $sConnInfo" }
        logger.info { "sMobileNo $sMobileNo" }
        logger.info { "sMobileCo $sMobileCo" }
        return niceUserInfo
    }

    fun getAuthFailInfo(
        httpSession: HttpSession,
        encodeData: String
    ): String {
        val niceCheck = NiceID.Check.CPClient()
        val result = niceCheck.fnDecode(siteCode, sitePassword, encodeData)

        var plainData: String
        //var cipherTime = ""
        var sRequestNumber = ""
        var sErrorCode = ""
        var sAuthType = ""
        var sMessage = ""

        when(result) {
            0 -> {
                plainData = niceCheck.plainData
                //cipherTime = niceCheck.cipherDateTime
                var mapResult = niceCheck.fnParse(plainData)
                sRequestNumber = mapResult.get("REQ_SEQ").toString()
                sErrorCode = mapResult.get("ERR_CODE").toString()
                sAuthType = mapResult.get("AUTH_TYPE").toString()
            }
            -1 -> { sMessage = "복호화 시스템 오류입니다." }
            -4 -> { sMessage = "복호화 처리 오류입니다." }
            -5 -> { sMessage = "복호화 해쉬 오류입니다." }
            -6 -> { sMessage = "복호화 데이터 오류입니다." }
            -9 -> { sMessage = "입력 데이터 오류입니다." }
            -12 -> { sMessage = "사이트 패스워드 오류입니다." }
            else -> { sMessage = "알수 없는 에러 입니다. iReturn : $result" }
        }
        logger.info { "result $result sMessage $sMessage" }
        logger.info { "sRequestNumber $sRequestNumber" }
        logger.info { "sAuthType $sAuthType" }
        logger.info { "sErrorCode $sErrorCode" }
        return sMessage
    }
}