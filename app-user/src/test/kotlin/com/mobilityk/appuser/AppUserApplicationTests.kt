package com.mobilityk.appuser

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AppUserApplicationTests {

    @Test
    fun contextLoads() {
        val sendPrice = 500000
        val basePrice = 1000000
        val gi = 300000

        println((sendPrice.toDouble() / basePrice.toDouble() * gi).toLong())

    }
}
