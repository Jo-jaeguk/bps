package com.mobilityk.core.config

import org.apache.http.impl.client.HttpClientBuilder
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.CollectionUtils
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect
import java.nio.charset.StandardCharsets
import java.util.Locale

@Configuration
class AppConfiguration {

    @Value("\${remote.maxTotalConnect}")
    val maxTotalConnect: Int = 0
    @Value("\${remote.maxConnectPerRoute}")
    val maxConnectPerRoute: Int = 200
    @Value("\${remote.connectTimeout}")
    val connectTimeout: Int = 2000
    @Value("\${remote.readTimeout}")
    val readTimeout: Int = 3000

    /**
     * Create HTTP Client Factory
     */
    private fun createFactory() : ClientHttpRequestFactory {
        if (this.maxTotalConnect <= 0) {
            val factory = SimpleClientHttpRequestFactory()
            factory.setReadTimeout(this.readTimeout)
            factory.setConnectTimeout(this.connectTimeout)
            return factory
        }
        val httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(this.maxTotalConnect)
            .setMaxConnPerRoute(this.maxConnectPerRoute).build()
        val factory = HttpComponentsClientHttpRequestFactory(httpClient)
        factory.setConnectTimeout(this.connectTimeout)
        factory.setReadTimeout(this.readTimeout)
        return factory
    }

    @Bean
    public fun getModelMapper() : ModelMapper = ModelMapper()

    @Bean
    public fun passwordEncoder() : PasswordEncoder {
        //var bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
        var delegatingPasswordEncoder: DelegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder() as DelegatingPasswordEncoder
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(BCryptPasswordEncoder())
        return delegatingPasswordEncoder
    }

    @Bean
    fun springSecurityDialect(): SpringSecurityDialect? {
        return SpringSecurityDialect()
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val sessionLocaleResolver = SessionLocaleResolver()
        sessionLocaleResolver.setDefaultLocale(Locale.KOREA)
        return sessionLocaleResolver
    }

    @Bean
    fun restTemplate() : RestTemplate {
        val restTemplate = RestTemplate(this.createFactory())
        val converterList : MutableList<HttpMessageConverter<*>> = restTemplate.messageConverters
        var converterTarget : HttpMessageConverter<*>? = null
        /**
         * Setting String HttpMessageConverter Character Set to UTF-8 resolves the problem of Chinese scrambling
         */
        for (item: HttpMessageConverter<*> in converterList) {
            if (StringHttpMessageConverter::class.java == item.javaClass) {
                converterTarget = item
                break
            }
        }
        if (null != converterTarget) {
            converterList.remove(converterTarget)
        }
        converterList.add(1, StringHttpMessageConverter(StandardCharsets.UTF_8))


        var interceptors = restTemplate.interceptors
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = ArrayList()
        }
        interceptors.add(RestTemplateHeaderModifierInterceptor())
        restTemplate.interceptors = interceptors

        return restTemplate
    }



}