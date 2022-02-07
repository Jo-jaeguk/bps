package com.mobilityk.appadmin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.mobilityk"])
@EnableJpaRepositories(basePackages = ["com.mobilityk"])
@EnableJpaAuditing
@EntityScan("com.mobilityk")
class AppAdminApplication

fun main(args: Array<String>) {
    runApplication<AppAdminApplication>(*args)
}
