package com.mobilityk.core.config

import com.mobilityk.core.repository.RememberMeTokenRepository
import com.mobilityk.core.service.SecurityLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class MyWebSecurity : WebSecurityConfigurerAdapter() {

    private final val REMEMBER_ME_KEY = "bps"

    @Autowired
    lateinit var customAuthenticationProvider: CustomAuthenticationProvider

    @Autowired
    lateinit var securityLoginService: SecurityLoginService

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var rememberMeTokenRepository: RememberMeTokenRepository


    override fun configure(http: HttpSecurity?) {

        http?.let {
            it.csrf()?.disable()
            it.authorizeRequests().antMatchers("/static/** , /js/** , /css/** , /main/** , /join/**, /login/** , /app/**, /nice/**, /kakao/**, /naver/**, /error/**").permitAll()
            it.authorizeRequests().antMatchers("/api/user/v1/member/join").permitAll()
            it.authorizeRequests()
                .antMatchers("/admin/**").hasAnyRole("MASTER,ADMIN")
                .antMatchers("/user/**" , "/api/**", "/ajax/**").hasAnyRole("MASTER,ADMIN,DEALER,NEW")
                .and()
                .formLogin().loginPage("/login")
                .loginProcessingUrl("/loginProcess")
                .usernameParameter("id")
                .passwordParameter("pw")
                .defaultSuccessUrl("/main") // main controller 에서 principle 권한을 체크하고 redirect 시키자.
                .failureUrl("/login?error=true")
                .successHandler(LoginSuccessHandler())
                .and()
                .logout().logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .rememberMe()
                .key(REMEMBER_ME_KEY)
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(86400 * 14)
                .rememberMeCookieName("remember-me")
                .tokenRepository(rememberMeTokenRepository)
                .userDetailsService(securityLoginService)
                .rememberMeServices(persistentTokenBasedRememberMeService())
                //.authenticationProvider(customAuthenticationProvider)

        }

    }

    @Bean
    fun persistentTokenBasedRememberMeService(): PersistentTokenBasedRememberMeServices {
        val persistenceTokenBasedService = PersistentTokenBasedRememberMeServices(
            REMEMBER_ME_KEY,
            securityLoginService,
            rememberMeTokenRepository
        )
        persistenceTokenBasedService.parameter = "remember-me"
        persistenceTokenBasedService.setAlwaysRemember(false)
        persistenceTokenBasedService.setCookieName("remember-me")
        persistenceTokenBasedService.setTokenValiditySeconds(86400 * 14)
        return persistenceTokenBasedService
    }


}