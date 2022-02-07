package com.mobilityk.core.service

import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.domain.buyandsell.Buy
import com.mobilityk.core.domain.buyandsell.BuyAndSell
import com.mobilityk.core.domain.buyandsell.BuyAndSellSearch
import com.mobilityk.core.domain.buyandsell.BuyItem
import com.mobilityk.core.domain.buyandsell.BuyType
import com.mobilityk.core.domain.buyandsell.CarLocation
import com.mobilityk.core.domain.buyandsell.ItemType
import com.mobilityk.core.domain.buyandsell.NewCarDealerLocation
import com.mobilityk.core.domain.buyandsell.NewCarDealerName
import com.mobilityk.core.domain.buyandsell.Sell
import com.mobilityk.core.domain.buyandsell.SellTarget
import com.mobilityk.core.domain.buyandsell.Seller
import com.mobilityk.core.dto.BuyTypeDTO
import com.mobilityk.core.dto.NewCarDealerLocationDTO
import com.mobilityk.core.dto.NewCarDealerNameDTO
import com.mobilityk.core.dto.buyandsell.BuyAndSellDTO
import com.mobilityk.core.dto.buyandsell.BuyDTO
import com.mobilityk.core.dto.buyandsell.BuyItemDTO
import com.mobilityk.core.dto.buyandsell.CarLocationDTO
import com.mobilityk.core.dto.buyandsell.SellDTO
import com.mobilityk.core.dto.buyandsell.SellTargetDTO
import com.mobilityk.core.dto.buyandsell.SellerDTO
import com.mobilityk.core.dto.mapper.BuyAndSellMapper
import com.mobilityk.core.dto.mapper.BuyItemMapper
import com.mobilityk.core.dto.mapper.BuyMapper
import com.mobilityk.core.dto.mapper.BuyTypeMapper
import com.mobilityk.core.dto.mapper.CarLocationMapper
import com.mobilityk.core.dto.mapper.NewCarDealerLocationMapper
import com.mobilityk.core.dto.mapper.NewCarDealerNameMapper
import com.mobilityk.core.dto.mapper.SellMapper
import com.mobilityk.core.dto.mapper.SellTargetMapper
import com.mobilityk.core.dto.mapper.SellerMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.GuideRepository
import com.mobilityk.core.repository.buyandsell.BuyAndSellRepository
import com.mobilityk.core.repository.buyandsell.BuyItemRepository
import com.mobilityk.core.repository.buyandsell.BuyRepository
import com.mobilityk.core.repository.buyandsell.BuyTypeRepository
import com.mobilityk.core.repository.buyandsell.CarLocationRepository
import com.mobilityk.core.repository.buyandsell.NewCarDealerLocationRepository
import com.mobilityk.core.repository.buyandsell.NewCarDealerNameRepository
import com.mobilityk.core.repository.buyandsell.SellRepository
import com.mobilityk.core.repository.buyandsell.SellTargetRepository
import com.mobilityk.core.repository.buyandsell.SellerRepository
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class BuyAndSellService(
    private val buyAndSellRepository: BuyAndSellRepository,
    private val buyItemRepository: BuyItemRepository,
    private val buyTypeRepository: BuyTypeRepository,
    private val guideRepository: GuideRepository,
    private val newCarDealerNameRepository: NewCarDealerNameRepository,
    private val newCarDealerLocationRepository: NewCarDealerLocationRepository,
    private val carLocationRepository: CarLocationRepository,
    private val buyRepository: BuyRepository,
    private val sellRepository: SellRepository,
    private val sellTargetRepository: SellTargetRepository,
    private val sellerRepository: SellerRepository,
    private val newCarDealerNameMapper: NewCarDealerNameMapper,
    private val newCarDealerLocationMapper: NewCarDealerLocationMapper,
    private val carLocationMapper: CarLocationMapper,
    private val buyAndSellMapper: BuyAndSellMapper,
    private val buyItemMapper: BuyItemMapper,
    private val buyTypeMapper: BuyTypeMapper,
    private val sellTargetMapper: SellTargetMapper,
    private val buyMapper: BuyMapper,
    private val sellMapper: SellMapper,
    private val sellerMapper: SellerMapper,
) {

    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun findAllBySearch(search: BuyAndSellSearch, pageable: Pageable): Page<BuyAndSellDTO> {
        return buyAndSellRepository.findAllBySearch(search, pageable)
    }

    @Transactional(readOnly = true)
    fun findAllBySearchExcel(search: BuyAndSellSearch, pageable: Pageable): Page<BuyAndSellDTO> {
        return buyAndSellRepository.findAllBySearchExcel(search, pageable)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): BuyAndSellDTO {
        val buyAndSell = buyAndSellRepository.findById(id).orElseThrow { CommException("not found") }
        val buyAndSellDTO = buyAndSellMapper.toDto(buyAndSell)
        buyAndSellDTO.guide = guideRepository.findByGuideId(buyAndSell.guideId!!)
        buyAndSellDTO.guide?.convertData()
        return buyAndSellDTO
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateBuyAndSellPrice(id: Long): BuyAndSellDTO {
        val buyAndSell = buyAndSellRepository.findById(id).orElseThrow { CommException("not found") }
        val sellList = sellRepository.findAllByBuySellOrderByOrderIndexDesc(buyAndSell)
        val buyList = buyRepository.findAllByBuySell(buyAndSell)
        val guide = guideRepository.findById(buyAndSell.guideId!!).orElseThrow { CommException("not found") }

        if(guide.retailAvgPrice == null) throw CommException("소매가 평균 값이 없습니다.")

        //var stepPrice: Long
        var stepPrice = when {
            guide.retailAvgPrice!! <= 5000000 -> {
                1000000
            }
            guide.retailAvgPrice!! in 5000001..10000000 -> {
                1200000
            }
            guide.retailAvgPrice!! in 10000001..15000000 -> {
                1500000
            }
            guide.retailAvgPrice!! in 15000001..20000000 -> {
                2000000
            }
            guide.retailAvgPrice!! in 20000001..30000000 -> {
                3000000
            }
            else -> {
                4000000
            }
        }
        buyAndSell.sellExpectPrice = guide.retailAvgPrice!! - stepPrice
        var bidSuccessPrice = 0L
        if(!sellList.isNullOrEmpty()) {
            val sell = sellList[0]
            buyAndSell.sellSellPrice = sell.sellPrice
            buyAndSell.sellOtherCommission = sell.otherCommission
            buyAndSell.sellHopePrice = sell.hopePrice
            buyAndSell.sellDiffPrice = buyAndSell.sellExpectPrice!! - (sell.sellPrice!! + sell.otherCommission!!)
            buyAndSell.sellTotalPrice = sell.otherCommission!! + sell.hopePrice!!

            bidSuccessPrice = sell.sellPrice!! + sell.otherCommission!!

        } else {
            buyAndSell.sellSellPrice = 0L
            buyAndSell.sellOtherCommission = 0L
            buyAndSell.sellHopePrice = 0L
            buyAndSell.sellDiffPrice = 0L
            buyAndSell.sellTotalPrice = 0L
        }

        var buyTotalPrice = 0L
        var sellTotalPrice = 0L
        var carPrice = 0L
        buyList.forEach { buyDTO ->
            val buyItemDTO = findBuyItemByItemTypeAndName(buyDTO.itemType!!, buyDTO.name!!)
            if(buyItemDTO != null) {
                sellTotalPrice += buyDTO.price!!
                if(buyDTO.itemType == ItemType.BUY_BASE_COST) {
                    buyTotalPrice += buyDTO.price!!
                    if(buyDTO.name == "차량대금") {
                        carPrice = buyDTO.price!!
                    }
                }
            }
        }
        buyAndSell.buyTotalPrice = buyTotalPrice

        buyAndSell.sellTotalPrice = sellTotalPrice

        buyAndSell.carPrice = carPrice

        buyAndSell.bidSuccessPrice = buyAndSell.sellSellPrice!! + buyAndSell.sellOtherCommission!!

        buyAndSell.sellRevenue = bidSuccessPrice - sellTotalPrice

        buyAndSell.expectRevenue = buyAndSell.sellExpectPrice!! - buyAndSell.buyTotalPrice!!

        buyAndSell.sellRevenueDiff = buyAndSell.sellRevenue

        return buyAndSellMapper.toDto(buyAndSellRepository.save(buyAndSell))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun saveBuyAndSell(id: Long, buyAndSellDTO: BuyAndSellDTO): BuyAndSellDTO {
        val buyAndSell = buyAndSellRepository.findById(id).orElseThrow { CommException("not found") }
        buyAndSell.buyerId = buyAndSellDTO.buyerId
        buyAndSell.buyType = buyAndSellDTO.buyType
        buyAndSell.newCarDealerLocation = buyAndSellDTO.newCarDealerLocation
        buyAndSell.newCarDealerName = buyAndSellDTO.newCarDealerName
        buyAndSell.modifyCarNumber = buyAndSellDTO.modifyCarNumber


        buyAndSell.successedAt = buyAndSellDTO.successedAt
        if(buyAndSellDTO.depositedAt != null) {
            buyAndSell.depositedAt = buyAndSellDTO.depositedAt
            val guide = guideRepository.findById(buyAndSell.guideId!!).orElseThrow { CommException("not found guide") }
            guide.updateGuideStatus(GuideStatus.SELL)
        }

        buyAndSell.seller = buyAndSellDTO.seller
        buyAndSell.carLocation = buyAndSellDTO.carLocation

        // 매입 아이템
        buyAndSellDTO.buyList?.forEach { buyDTO ->

            //logger.info { "${buyDTO.itemType} ${buyDTO.name} ${buyDTO.price}" }

            //val buyItem = buyItemRepository.findById(buyDTO.id!!).orElseThrow { CommException("not found buy item") }
            val buyOpt = buyRepository.findByBuySellAndItemTypeAndName(buyAndSell, buyDTO.itemType!!, buyDTO.name!!)

            if(buyOpt.isPresent) {
                val buy = buyOpt.get();
                buy.price = buyDTO.price
                buyRepository.save(buy)
            } else {
                buyRepository.save(
                    Buy(
                        buySell = buyAndSell,
                        itemType = buyDTO.itemType,
                        name = buyDTO.name,
                        price = buyDTO.price
                    )
                )
            }
        }

        // 매출 아이템
        buyAndSellDTO.sellList?.forEach { sellDTO ->
            if(sellDTO.id != null && sellDTO.id!! > 0) {
                val sell = sellRepository.findById(sellDTO.id!!).orElseThrow { CommException("not found sell") }
                sell.update(sellDTO)
                sellRepository.save(sell)
            } else {
                val sell = Sell()
                sell.create(buyAndSell, sellDTO)
                sellRepository.save(sell)
            }

        }
        return buyAndSellMapper.toDto(buyAndSellRepository.save(buyAndSell))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createBuyItem(buyItemDTO: BuyItemDTO): BuyItemDTO {
        val buyItemList = buyItemRepository.findAllByItemTypeAndName(buyItemDTO.itemType!!, buyItemDTO.name!!)
        if(buyItemList.isNullOrEmpty()) {
            return buyItemMapper.toDto(
                buyItemRepository.save(
                    BuyItem(
                        itemType = buyItemDTO.itemType,
                        name = buyItemDTO.name
                    )
                )
            )
        } else {
            throw CommException("동일한 아이템명이 존재합니다.")
        }
    }

    @Transactional(readOnly = true)
    fun getBuyItemsByItemType(itemType: ItemType): List<BuyItemDTO> {
        return buyItemMapper.toDtoList(buyItemRepository.findAllByItemType(itemType))
    }

    @Transactional(readOnly = true)
    fun getBuyItemsByBuyAndSellAndItemType(buyAndSellId: Long, itemType: ItemType): List<BuyItemDTO> {
        buyAndSellRepository.findById(buyAndSellId).orElseThrow { CommException("not found buy and sell") }
        return buyItemMapper.toDtoList(buyItemRepository.findAllByItemType(itemType))
    }

    @Transactional(readOnly = true)
    fun findBuyItemByItemTypeAndName(itemType: ItemType, name: String): BuyItemDTO? {
        val buyItemList = buyItemRepository.findAllByItemTypeAndName(itemType, name)
        return if(buyItemList.isNullOrEmpty()) {
            null
        } else {
            buyItemMapper.toDto(buyItemList[0])
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteBuyItem(id: Long) {
        val buyItem = buyItemRepository.findById(id).orElseThrow { CommException("존재하지 않는 항목입니다.") }
        when(buyItem.name) {
            "차량대금" -> CommException("삭제할 수 없는 항목입니다.")
            "딜러수수료" -> CommException("삭제할 수 없는 항목입니다.")
            "경매수수료" -> CommException("삭제할 수 없는 항목입니다.")
            "성능점검" -> CommException("삭제할 수 없는 항목입니다.")
            "이전등록비" -> CommException("삭제할 수 없는 항목입니다.")
            "탁송비" -> CommException("삭제할 수 없는 항목입니다.")
        }
        buyItemRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createBuyType(buyTypeDTO: BuyTypeDTO): BuyTypeDTO {
        return buyTypeMapper.toDto(
            buyTypeRepository.save(
                BuyType(
                    name = buyTypeDTO.name
                )
            )
        )
    }


    @Transactional(readOnly = true)
    fun getBuyList(buyAndSellId: Long): List<BuyDTO> {
        val buyAndSell = buyAndSellRepository.findById(buyAndSellId).orElseThrow { CommException("not found") }
        return buyMapper.toDtoList(buyRepository.findAllByBuySell(buyAndSell))
    }

    @Transactional(readOnly = true)
    fun getBuyListByGuideId(guideId: Long): List<BuyDTO>? {
        logger.info { "guideId $guideId" }
        val buyAndSell = buyAndSellRepository.findByGuideId(guideId)
        return if(buyAndSell.isPresent) {
            buyMapper.toDtoList(buyRepository.findAllByBuySell(buyAndSell = buyAndSell.get()))
        } else {
            null
        }
    }

    @Transactional(readOnly = true)
    fun getBuyItem(buyAndSellId: Long, itemType: ItemType, name: String): BuyDTO? {
        val buyAndSell = buyAndSellRepository.findById(buyAndSellId).orElseThrow { CommException("not found") }
        val buyOpt = buyRepository.findByBuySellAndItemTypeAndName(buyAndSell, itemType, name)
        if(buyOpt.isPresent) {
            return buyMapper.toDto(buyOpt.get())
        } else {
            return null
        }
    }

    @Transactional(readOnly = true)
    fun getSellList(buyAndSellId: Long): List<SellDTO> {
        val buyAndSell = buyAndSellRepository.findById(buyAndSellId).orElseThrow { CommException("not found") }
        return sellMapper.toDtoList(sellRepository.findAllByBuySellOrderByOrderIndexDesc(buyAndSell))
    }

    @Transactional(readOnly = true)
    fun getSellListByGuideId(guideId: Long): List<SellDTO>? {
        val buyAndSellOpt = buyAndSellRepository.findByGuideId(guideId)
        if(buyAndSellOpt.isPresent) {
            return sellMapper.toDtoList(
                sellRepository.findAllByBuySellOrderByOrderIndexDesc(buyAndSell = buyAndSellOpt.get())
            )
        } else {
            return null
        }
    }

    @Transactional(readOnly = true)
    fun getBuyTypes(): List<BuyTypeDTO> {
        return buyTypeMapper.toDtoList(buyTypeRepository.findAll())
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteBuyType(id: Long) {
        buyTypeRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createNewCarDealerName(newCarDealerNameDTO: NewCarDealerNameDTO): NewCarDealerNameDTO {
        return newCarDealerNameMapper.toDto(
            newCarDealerNameRepository.save(
                NewCarDealerName(
                    name = newCarDealerNameDTO.name
                )
            )
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getnewCarDealerNames(): List<NewCarDealerNameDTO> {
        return newCarDealerNameMapper.toDtoList(
            newCarDealerNameRepository.findAll()
        )
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteNewCarDealerNames(id: Long) {
        newCarDealerNameRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createNewCarDealerLocation(newCarDealerLocationDTO: NewCarDealerLocationDTO): NewCarDealerLocationDTO {
        return newCarDealerLocationMapper.toDto(
            newCarDealerLocationRepository.save(
                NewCarDealerLocation(
                    name = newCarDealerLocationDTO.name
                )
            )
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getnewCarDealerLocations(): List<NewCarDealerLocationDTO> {
        return newCarDealerLocationMapper.toDtoList(
            newCarDealerLocationRepository.findAll()
        )
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteNewCarDealerLocation(id: Long) {
        newCarDealerLocationRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createCarLocation(carLocationDTO: CarLocationDTO): CarLocationDTO {
        return carLocationMapper.toDto(
            carLocationRepository.save(
                CarLocation(
                    name = carLocationDTO.name
                )
            )
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getCarLocations(): List<CarLocationDTO> {
        return carLocationMapper.toDtoList(
            carLocationRepository.findAll()
        )
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteCarLocation(id: Long) {
        carLocationRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createSellTarget(sellTargetDTO: SellTargetDTO): SellTargetDTO {
        return sellTargetMapper.toDto(
            sellTargetRepository.save(
                SellTarget(
                    name = sellTargetDTO.name
                )
            )
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getSellTargets(): List<SellTargetDTO> {
        return sellTargetMapper.toDtoList(sellTargetRepository.findAll())
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteSellTarget(id: Long) {
        sellTargetRepository.deleteById(id)
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createSeller(sellerDTO: SellerDTO): SellerDTO {
        return sellerMapper.toDto(
            sellerRepository.save(
                Seller(
                    name = sellerDTO.name
                )
            )
        )
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun getSellers(): List<SellerDTO> {
        return sellerMapper.toDtoList(sellerRepository.findAll())
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteSeller(id: Long) {
        sellerRepository.deleteById(id)
    }

}