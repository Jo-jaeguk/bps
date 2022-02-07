package com.mobilityk.appuser.web

import com.mobilityk.core.domain.AccountType
import com.mobilityk.core.dto.api.nice.NiceAuthDTO
import com.mobilityk.core.service.KakaoTalkService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.NiceAuthService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/nice")
data class NicePhonePageController(
    private val memberService: MemberService,
    private val niceAuthService: NiceAuthService,
    private val kakaoTalkService: KakaoTalkService,
    private val messageSource: MessageSource
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/auth")
    fun auth(
        @RequestParam("accountType", required = false) accountType: AccountType?,
        @RequestParam("id", required = false ) id: Long?,
        @RequestParam("callback_uri", required = false) callBackUri: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        val encData = niceAuthService.getJoinEncData(httpSession, callBackUri, accountType, id)
        logger.info { "/nice/auth accountType[$accountType] id[$id] callBackUri[$callBackUri]" }
        logger.info { "encData" }
        logger.info { "$encData" }
        model["encData"] = encData
        model["id"] = id.toString()
        if (accountType != null) {
            model["accountType"] = accountType.name
        }
        val url = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?EncodeData=$encData&m=checkplusService"
        //return ModelAndView("nice/auth" , model)
        return ModelAndView("redirect:$url" , model)
    }

    @GetMapping("/auth/change/phone")
    fun changePhone(
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "/nice/auth/change/phone memberId[${user.username.toLong()}]" }
        val encData = niceAuthService.getChangePhoneEncData(httpSession, user.username.toLong())
        logger.info { "encData" }
        logger.info { "$encData" }
        model["encData"] = encData
        val url = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?EncodeData=$encData&m=checkplusService"
        //return ModelAndView("nice/auth" , model)
        return ModelAndView("redirect:$url" , model)
    }

    @GetMapping("/auth/change/password")
    fun changePassword(
        @AuthenticationPrincipal user: User,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "/nice/auth/change/password memberId[${user.username.toLong()}]" }
        val encData = niceAuthService.getChangePasswordEncData(httpSession, user.username.toLong())
        logger.info { "encData" }
        logger.info { "$encData" }
        model["encData"] = encData
        val url = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?EncodeData=$encData&m=checkplusService"
        //return ModelAndView("nice/auth" , model)
        return ModelAndView("redirect:$url" , model)
    }

    @GetMapping("/join/success")
    fun success(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        @RequestParam("accountType", required = false) accountType: AccountType?,
        @RequestParam("id", required = false) id: Long?,
        @RequestParam("callback_uri", required = false) callBackUri: String?,
        @RequestBody(required = false) niceAuthDTO: NiceAuthDTO?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {

        logger.info { "GET /nice/join/success encodeData[$encodeData] accountType[$accountType] id[$id] callBackUri[$callBackUri]" }
        logger.info { "GET /nice/join/success body [${niceAuthDTO?.encodeData}]" }

        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData!!)
            val memberDTO = memberService.joinSuccess(
                niceUserInfo = niceUserInfo,
                accountType = accountType!!,
                accountId = id!!
            )

            kakaoTalkService.sendKakaoTalk(
                targetMemberDTO = memberDTO,
                sendPhone = memberDTO.phone!!,
                titleKey = "join.title",
                messageKey = "join.body",
                templateCodeKey = "join.template",
                btnUrlKey = null,
                btnTextKey = null
            )

            model["success"] = "join success"
        } catch (e: Exception) {
            logger.info { "GET /nice/join/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }

        return ModelAndView("join/join" , model)
    }

    @GetMapping("/join/fail")
    fun fail(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        @RequestParam("accountType") accountType: AccountType,
        @RequestParam("id") id: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        //model["encData"] = niceAuthService.getEncData(httpSession)
        logger.info { "GET /nice/join/fail" }
        niceAuthService.getAuthFailInfo(httpSession, encodeData!!)
        model["error"] = "join fail"
        model["message"] = "join fail"
        return ModelAndView("join/join" , model)
    }


    @GetMapping("/search/id/success")
    fun searchIdSuccess(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {

        logger.info { "GET /nice/search/id/success" }

        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData!!)
            val memberDTO = memberService.findByPhone(niceUserInfo.phone!!)
            var searchId: String = ""
            when(memberDTO.accountType) {
                AccountType.KAKAO -> {
                    searchId = "카카오계정"
                }
                AccountType.NAVER -> {
                    searchId = "네이버계정"
                }
                AccountType.APPLE -> {
                    searchId = "애플계정"
                }
                AccountType.EMAIL -> {
                    searchId = memberDTO.emailAddress!!
                }
            }

            kakaoTalkService.sendKakaoTalk(
                targetMemberDTO = memberDTO,
                sendPhone = memberDTO.phone!!,
                titleKey = "search.id.title",
                messageKey = "search.id.body",
                templateCodeKey = "search.id.template",
                btnUrlKey = null,
                btnTextKey = null,
                messageList = listOf(searchId)
            )

            model["success"] = "join success"
        } catch (e: Exception) {
            model["error"] = e.message.toString()
            logger.info { "GET /nice/search/id/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
        }
        return ModelAndView("redirect:/login/email" , model)
    }

    @GetMapping("/search/password/success")
    fun searchPasswordSuccess(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "GET /nice/search/password/success" }
        var redirectUrl = "/login/search/password"
        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData!!)
            val memberDTO = memberService.findByPhone(niceUserInfo.phone!!)
            model["memberId"] = memberDTO.id!!
            redirectUrl += "?memberId=${memberDTO.id}&result=success"
            model["success"] = "join success"
        } catch (e: Exception) {
            model["error"] = e.message.toString()
            redirectUrl += "?memberId=-1&result=not found user"
            logger.info { "GET /nice/search/password/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
        }
        return ModelAndView("redirect:${redirectUrl}" , model)
    }

    @GetMapping("/search/id/fail")
    fun searchFail(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        //model["encData"] = niceAuthService.getEncData(httpSession)
        logger.info { "GET /nice/search/id/fail" }
        niceAuthService.getAuthFailInfo(httpSession, encodeData!!)
        model["error"] = "join fail"
        model["message"] = "join fail"
        return ModelAndView("login/email" , model)
    }

    @GetMapping("/search/password/fail")
    fun searchPasswordFail(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "GET /nice/search/password/fail" }
        //model["encData"] = niceAuthService.getEncData(httpSession)
        niceAuthService.getAuthFailInfo(httpSession, encodeData!!)
        model["error"] = "join fail"
        model["message"] = "join fail"
        return ModelAndView("login/search_password" , model)
    }


    @PostMapping("/join/success")
    fun successP(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        @RequestParam("accountType", required = false) accountType: AccountType?,
        @RequestParam("id", required = false) id: Long?,
        @RequestParam("callback_uri", required = false) callBackUri: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "POST /nice/join/success accountType[$accountType] id[$id] callBackUri[$callBackUri]" }
        logger.info { "POST /nice/join/success encodeData[$encodeData]" }

        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData!!)
            val memberDTO = memberService.joinSuccess(
                niceUserInfo = niceUserInfo,
                accountType = accountType!!,
                accountId = id!!
            )
            kakaoTalkService.sendKakaoTalk(
                targetMemberDTO = memberDTO,
                sendPhone = memberDTO.phone!!,
                titleKey = "join.title",
                messageKey = "join.body",
                templateCodeKey = "join.template",
                btnUrlKey = null,
                btnTextKey = null
            )
            model["success"] = "join success"
        } catch (e: Exception) {
            model["error"] = e.message.toString()
            logger.info { "POST /nice/join/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
        }

        return ModelAndView("join/join" , model)
    }

    @PostMapping("/join/fail")
    fun failP(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        @RequestParam("accountType") accountType: AccountType,
        @RequestParam("id") id: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        //model["encData"] = niceAuthService.getEncData(httpSession)
        logger.info { "POST /nice/join/fail" }
        niceAuthService.getAuthFailInfo(httpSession, encodeData!!)
        model["error"] = "join fail"
        model["message"] = "join fail"
        return ModelAndView("join/join" , model)
    }


    @PostMapping("/search/id/success")
    fun searchIdSuccessP(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "POST /nice/search/id/success" }
        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData!!)
            val memberDTO = memberService.findByPhone(niceUserInfo.phone!!)
            var searchId: String = ""
            when(memberDTO.accountType) {
                AccountType.KAKAO -> {
                    searchId = "카카오계정"
                }
                AccountType.NAVER -> {
                    searchId = "네이버계정"
                }
                AccountType.APPLE -> {
                    searchId = "애플계정"
                }
                AccountType.EMAIL -> {
                    searchId = memberDTO.emailAddress!!
                }
            }
            kakaoTalkService.sendKakaoTalk(
                targetMemberDTO = memberDTO,
                sendPhone = memberDTO.phone!!,
                titleKey = "search.id.title",
                messageKey = "search.id.body",
                templateCodeKey = "search.id.template",
                btnUrlKey = null,
                btnTextKey = null,
                messageList = listOf(searchId)
            )
            model["success"] = "join success"
        } catch (e: Exception) {
            model["error"] = e.message.toString()
            logger.info { "POST /nice/search/id/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
        }
        return ModelAndView("redirect:/login/email" , model)
    }

    @PostMapping("/search/password/success")
    fun searchPasswordSuccessP(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        var redirectUrl = "/login/search/password"
        logger.info { "POST /nice/search/password/success" }
        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData!!)
            val memberDTO = memberService.findByPhone(niceUserInfo.phone!!)
            model["memberId"] = memberDTO.id!!
            redirectUrl += "?memberId=${memberDTO.id}&result=success"
            model["success"] = "join success"
        } catch (e: Exception) {
            model["error"] = e.message.toString()
            logger.info { "POST /nice/search/password/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            redirectUrl += "?memberId=-1&result=not found user"
        }
        return ModelAndView("redirect:${redirectUrl}" , model)
    }

    @PostMapping("/search/id/fail")
    fun searchFailP(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        //model["encData"] = niceAuthService.getEncData(httpSession)
        logger.info { "POST /nice/search/id/fail" }
        niceAuthService.getAuthFailInfo(httpSession, encodeData!!)
        model["error"] = "join fail"
        model["message"] = "join fail"
        return ModelAndView("login/email" , model)
    }

    @PostMapping("/search/password/fail")
    fun searchPasswordFailP(
        @RequestParam("EncodeData", required = false) encodeData: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "POST /nice/search/password/fail" }
        //model["encData"] = niceAuthService.getEncData(httpSession)
        niceAuthService.getAuthFailInfo(httpSession, encodeData!!)
        model["error"] = "join fail"
        model["message"] = "join fail"
        return ModelAndView("login/search_password" , model)
    }


    @GetMapping("/change/phone/success")
    fun changePhoneSuccess(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "GET /change/phone/success encodeData[$encodeData] memberId[$memberId]" }
        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData)
            memberService.changePersonalInfo(memberId, niceUserInfo)
            model["success"] = "join success"
        } catch (e: Exception) {
            logger.info { "GET /change/phone/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage" , model)
    }


    @PostMapping("/change/phone/success")
    fun changePhoneSuccessP(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "POST /change/phone/success encodeData[$encodeData] memberId[$memberId]" }
        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData)
            memberService.changePersonalInfo(memberId, niceUserInfo)
            model["success"] = "join success"
        } catch (e: Exception) {
            logger.info { "POST /change/phone/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage" , model)
    }

    @GetMapping("/change/phone/fail")
    fun changePhoneFail(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "GET /change/phone/fail encodeData[$encodeData] memberId[$memberId]" }
        try {
            niceAuthService.getAuthFailInfo(httpSession, encodeData)
            model["error"] = "change phone fail"
        } catch (e: Exception) {
            logger.info { "GET /change/phone/fail exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage" , model)
    }


    @PostMapping("/change/phone/fail")
    fun changePhoneFailP(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "POST /change/phone/fail encodeData[$encodeData] memberId[$memberId]" }
        try {
            niceAuthService.getAuthFailInfo(httpSession, encodeData)
            model["error"] = "change phone fail"
        } catch (e: Exception) {
            logger.info { "POST /change/phone/fail exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage" , model)
    }




    @GetMapping("/change/password/success")
    fun changePasswordSuccess(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "GET /change/password/success encodeData[$encodeData] memberId[$memberId]" }
        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData)
            memberService.changePersonalInfo(memberId, niceUserInfo)
            model["success"] = "join success"
        } catch (e: Exception) {
            logger.info { "GET /change/password/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage/change/password" , model)
    }


    @PostMapping("/change/password/success")
    fun changePasswordSuccessP(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "POST /change/password/success encodeData[$encodeData] memberId[$memberId]" }
        try {
            val niceUserInfo = niceAuthService.getAuthInfo(httpSession, encodeData)
            memberService.changePersonalInfo(memberId, niceUserInfo)
            model["success"] = "join success"
        } catch (e: Exception) {
            logger.info { "POST /change/password/success exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage/change/password" , model)
    }

    @GetMapping("/change/password/fail")
    fun changePasswordFail(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "GET /change/password/fail encodeData[$encodeData] memberId[$memberId]" }
        try {
            niceAuthService.getAuthFailInfo(httpSession, encodeData)
            model["error"] = "change phone fail"
        } catch (e: Exception) {
            logger.info { "GET /change/password/fail exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage/change/password" , model)
    }


    @PostMapping("/change/password/fail")
    fun changePasswordFailP(
        @RequestParam("EncodeData") encodeData: String,
        @RequestParam("memberId") memberId: Long,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "POST /change/password/fail encodeData[$encodeData] memberId[$memberId]" }
        try {
            niceAuthService.getAuthFailInfo(httpSession, encodeData)
            model["error"] = "change phone fail"
        } catch (e: Exception) {
            logger.info { "POST /change/password/fail exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            model["error"] = e.message.toString()
        }
        return ModelAndView("redirect:/user/mypage/change/password" , model)
    }





}