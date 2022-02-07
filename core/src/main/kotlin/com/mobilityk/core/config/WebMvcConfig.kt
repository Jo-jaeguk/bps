package com.mobilityk.core.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var appInterceptor: AppInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(appInterceptor)
            .excludePathPatterns("/login/**")
            .excludePathPatterns("/static/**")
            .excludePathPatterns("/js/**")
            .excludePathPatterns("/css/**")
            .addPathPatterns("/admin/**" , "/ajax/**" , "/api/**" , "/user/**")
    }
}