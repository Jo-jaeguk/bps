package com.mobilityk.appadmin

import com.mobilityk.core.domain.Branch
import com.mobilityk.core.domain.CarManufacturer
import com.mobilityk.core.domain.CarManufacturerName
import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.DamageLocation
import com.mobilityk.core.domain.Member
import com.mobilityk.core.domain.buyandsell.ItemType
import com.mobilityk.core.dto.DamageCheckConfigDTO
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.BranchRepository
import com.mobilityk.core.repository.CarManufacturerRepository
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.buyandsell.BuyAndSellRepository
import com.mobilityk.core.repository.buyandsell.BuyRepository
import com.mobilityk.core.repository.buyandsell.SellRepository
import com.mobilityk.core.service.BuyAndSellService
import com.mobilityk.core.service.CarManufacturerService
import com.mobilityk.core.service.DamageCheckService
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
data class AppRunner(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val carManufacturerRepository: CarManufacturerRepository,
    private val carManufacturerService: CarManufacturerService,
    private val damageCheckService: DamageCheckService,
    private val branchRepository: BranchRepository,
    private val buyAndSellRepository: BuyAndSellRepository,
    private val buyRepository: BuyRepository,
    private val sellRepository: SellRepository,
    private val buyAndSellService: BuyAndSellService
): CommandLineRunner{

    private val logger = KotlinLogging.logger {}

    override fun run(vararg args: String?) {

        val branchOpt = branchRepository.findByBranch("서서울지점")
        if(branchOpt.isEmpty){
            branchRepository.save(
                Branch(
                    branch = "서서울지점",
                    branchNumber = "01"
                )
            )
        } else {
            val branch = branchOpt.get()
            branch.branchNumber = "01"
            branchRepository.save(branch)
        }

        var email = "master_dev"
        var password = passwordEncoder.encode("master")
        var member = memberRepository.findByEmailAddress(email)
        if(member.isEmpty) {
            memberRepository.save(
                Member(
                    emailAddress = email,
                    password = password,
                    name = "관리자",
                    phone = "01111112222",
                    accessYn = true,
                    roles = mutableSetOf(ROLE.MASTER),
                    point = 0,
                    guideCount = 0
                )
            )
        }

        email = "greatyun"
        password = passwordEncoder.encode("1234")
        member = memberRepository.findByEmailAddress(email)
        if(member.isEmpty) {
            memberRepository.save(
                Member(
                    emailAddress = email,
                    password = password,
                    name = "윤지상",
                    phone = "01077777777",
                    accessYn = true,
                    roles = mutableSetOf(ROLE.ADMIN),
                    point = 0,
                    guideCount = 0
                )
            )
        } else {
            member.get().roles = mutableSetOf(ROLE.ADMIN)
        }
        //makeAdmin()
        //carManufacturerService.makeCarManufacturer()
        makeDamageConfig()
        //dataIntegration()
    }

    private fun dataIntegration() {

        val buyAndSellList = buyAndSellRepository.findAll()

        buyAndSellList.forEach { buyAndSell ->

            var carPrice = 0L
            var sellTotalPrice = 0L
            var buyTotalPrice = 0L
            var bidSuccessPrice = 0L

            val buyList = buyRepository.findAllByBuySell(buyAndSell)
            buyList.forEach { buy ->
                val buyItemDTO = buyAndSellService.findBuyItemByItemTypeAndName(buy.itemType!!, buy.name!!)
                if(buyItemDTO != null) {
                    sellTotalPrice += buy.price!!
                    if(buy.itemType == ItemType.BUY_BASE_COST) {
                        buyTotalPrice += buy.price!!
                        if(buy.name == "차량대금") {
                            carPrice = buy.price!!
                        }
                    }
                }
            }

            val sellList = buyAndSellService.getSellList(buyAndSell.id!!)
            if(sellList.isNullOrEmpty()) {
                bidSuccessPrice = 0L
            } else {
                val sellDTO = sellList[0]
                bidSuccessPrice = sellDTO.sellPrice!! + sellDTO.otherCommission!!
            }


            buyAndSell.carPrice = carPrice
            buyAndSell.buyTotalPrice = buyTotalPrice
            buyAndSell.sellTotalPrice = sellTotalPrice
            buyAndSell.bidSuccessPrice = bidSuccessPrice
            buyAndSell.sellRevenue = bidSuccessPrice - sellTotalPrice

            buyAndSellRepository.save(buyAndSell)
        }
    }

    private fun makeDamageConfig() {
        CarTypeEnum.values().forEach { carTypeEnum ->
            DamageLocation.values().forEach { damageLocation ->
                try {
                    //val damageCheckConfigDTO = damageCheckService.findByCarTypeAndDamageLocation(carTypeEnum, damageLocation)
                    damageCheckService.initDamageCheckConfig(
                        DamageCheckConfigDTO(
                            carType = carTypeEnum,
                            damageLocation = damageLocation,
                            damageLocationStr = damageLocation.description,
                            giPangumPrice = 0L,
                            giGyoHwanPrice = 0L,
                            giDosaekPrice = 0L,
                            needPangumPrice = 0L,
                            needGyoHwanPrice = 0L,
                            needDosaekPrice = 0L,
                            otherPrice = 0L,
                        )
                    )
                } catch (e: CommException) {
                    /*
                    val damageCheckConfigDTO = DamageCheckConfigDTO(
                        carType = carTypeEnum,
                        damageLocation = damageLocation,
                        damageLocationStr = damageLocation.description,
                        giPangumPrice = 0L,
                        giGyoHwanPrice = 0L,
                        giDosaekPrice = 0L,
                        needPangumPrice = 0L,
                        needGyoHwanPrice = 0L,
                        needDosaekPrice = 0L,
                        otherPrice = 0L,
                    )
                    damageCheckService.createDamageCheckConfig(damageCheckConfigDTO)
                     */
                }
            }
        }

    }


    private fun makeAdmin() {
        var email = "test1"
        var password = passwordEncoder.encode("1234")
        var member = memberRepository.findByEmailAddress(email)
        if(member.isEmpty) {
            memberRepository.save(
                Member(
                    emailAddress = email,
                    password = password,
                    name = "관리자",
                    phone = "01012341230",
                    accessYn = true,
                    roles = mutableSetOf(ROLE.ADMIN),
                    point = 0,
                    guideCount = 0
                )
            )
        }

        email = "test2"
        password = passwordEncoder.encode("1234")
        member = memberRepository.findByEmailAddress(email)
        if(member.isEmpty) {
            memberRepository.save(
                Member(
                    emailAddress = email,
                    password = password,
                    name = "관리자",
                    phone = "01012341231",
                    accessYn = true,
                    roles = mutableSetOf(ROLE.ADMIN),
                    point = 0,
                    guideCount = 0
                )
            )
        }

        email = "test3"
        password = passwordEncoder.encode("1234")
        member = memberRepository.findByEmailAddress(email)
        if(member.isEmpty) {
            memberRepository.save(
                Member(
                    emailAddress = email,
                    password = password,
                    name = "관리자",
                    phone = "01012341232",
                    accessYn = true,
                    roles = mutableSetOf(ROLE.ADMIN),
                    point = 0,
                    guideCount = 0
                )
            )
        }
    }

    fun makeMember() {
        for (index in 0 .. 100) {
            memberRepository.save(
                Member(
                    emailAddress = "TEST_index_$index",
                    password = passwordEncoder.encode("1234"),
                    name = "관리자",
                    accessYn = true,
                    guideCount = 0,
                    point = 0,
                    roles = mutableSetOf(ROLE.ADMIN)
                )
            )
        }
    }
    fun makeGuide() {

    }
}