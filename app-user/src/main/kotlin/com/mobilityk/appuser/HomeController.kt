package com.mobilityk.appuser

import com.mobilityk.core.service.MemberService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
data class HomeController(
    private val memberService: MemberService
) {
    @GetMapping("/")
    fun home(model: MutableMap<String, Any>): ModelAndView {
        return ModelAndView("redirect:/user/home" , model)
    }

    @GetMapping("/main")
    fun main(model: MutableMap<String, Any>): ModelAndView {
        return ModelAndView("redirect:/user/home" , model)
    }

    @GetMapping("/user/home")
    fun userHome(model: MutableMap<String, Any>): ModelAndView {
        model["link"] = "home"
        return ModelAndView("home/home" , model)
    }

}