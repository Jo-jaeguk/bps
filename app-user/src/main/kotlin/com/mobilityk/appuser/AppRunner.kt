package com.mobilityk.appuser

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.mobilityk.core.domain.CarManufacturerName
import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuidePrice
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.domain.Member
import com.mobilityk.core.domain.Notification
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarManufacturerRepository
import com.mobilityk.core.repository.CarModelRepository
import com.mobilityk.core.repository.CarTrimRepository
import com.mobilityk.core.repository.GuidePriceRepository
import com.mobilityk.core.repository.GuideRepository
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.NotificationRepository
import com.mobilityk.core.service.CarManufacturerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class AppRunner(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val guideRepository: GuideRepository,
    private val guidePriceRepository: GuidePriceRepository,
    private val carManufacturerRepository: CarManufacturerRepository,
    private val carManufacturerService: CarManufacturerService,
    private val notificationRepository: NotificationRepository,
    private val carModelRepository: CarModelRepository,
    private val carClassRepository: CarClassRepository,
    private val carTrimRepository: CarTrimRepository,
    @Value("classpath:")
    private val restApiKey: String,
): CommandLineRunner {
    override fun run(vararg args: String?) {
        var resource = ClassPathResource("firebase/buyingcar-376f3-firebase-adminsdk-9r3nw-32030e44e2.json")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
            .build()
        FirebaseApp.initializeApp(options)

        /*
        var email = "admin"
        var password = passwordEncoder.encode("1234")
        var member = memberRepository.findByEmailAddress(email)
        if(member.isEmpty) {
          memberRepository.save(
              Member(
                  emailAddress = email,
                  password = password,
                  name = "관리자",
                  accessYn = true,
                  phone = "01011112222",
                  roles = mutableSetOf(ROLE.ADMIN)
              )
          )
        }
         */

        //carManufacturerService.makeCarManufacturer()
        //guidemake()
        //notifiation()
        //createBulk()
    }

    private fun createBulk() {
        val resource = ClassPathResource("data/carlist.txt")
        val path = Paths.get(resource.uri)
        val dataList = Files.readAllLines(path)

        dataList.forEach { data ->
            val arrayStr = data.split("\t")
            if(arrayStr.size == 5) {
                val carModelName = arrayStr[0].trim()
                val carModelDetailName = arrayStr[1].trim()
                val carClassName = arrayStr[2].trim()
                val carDisplacement = arrayStr[3].trim()
                val popularType = PopularType.UN_POPULAR
                val trim = arrayStr[4].trim()
                println("[$carModelName] [$carModelDetailName] [$carClassName] [$carDisplacement] [$trim]")

                val carTrim = carManufacturerService.createBaseBulk(
                    carManufacturerName = CarManufacturerName.KIA,
                    carModelName = carModelName,
                    carModelDetailName = carModelDetailName,
                    carClassName = carClassName,
                    carDisplacement = carDisplacement.toInt(),
                    popularType = popularType,
                    carTrim = trim
                )
                if(carTrim != null) {
                    println("make success")
                } else {
                    println("make fail")
                }


            }
        }
    }

    private fun notifiation() {
        for (i in 0 .. 20) {
            notificationRepository.save(
                Notification(
                    title = "title$i",
                    body = "body$i",
                    readYn = i % 2 == 0,
                    memberId = 2,
                    writerId = 1
                )
            )
        }
    }

    fun guidemake() {
        for (i in 0 .. 20) {
            val guide = Guide(
                serial = "!234${i}",
                memberId = 1L,
                carManufacturer = "1234",
                carNumber = "1234",
                carModel = "1234",
                guideStatus = if(i % 2 == 0) GuideStatus.REQUEST else GuideStatus.FINISH
            )
            val save = guideRepository.save(guide)
            val guidePrice = GuidePrice(
                marketName = "1",
                price = 1000L,
                guide = save
            )
            guidePriceRepository.save(guidePrice)
        }
    }

}