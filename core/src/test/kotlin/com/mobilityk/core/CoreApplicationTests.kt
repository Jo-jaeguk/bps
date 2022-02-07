package com.mobilityk.core

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CoreApplicationTests {

    @Test
    fun contextLoads() {
        val sendPrice = 500000
        val basePrice = 1000000
        val gi = 500000

        println(sendPrice.toDouble() / basePrice.toDouble() * gi)


    }
}
