package com.mobilityk.core.service

import com.mobilityk.core.domain.CarClass
import com.mobilityk.core.domain.CarManufacturer
import com.mobilityk.core.domain.CarManufacturerName
import com.mobilityk.core.domain.CarManufacturerSearchOption
import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.domain.CarTrim
import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.dto.CarManufacturerDTO
import com.mobilityk.core.dto.api.carManufacturer.CarManufacturerAllVM
import com.mobilityk.core.dto.api.carManufacturer.CarManufacturerVM
import com.mobilityk.core.dto.mapper.CarManufacturerMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarManufacturerRepository
import com.mobilityk.core.repository.CarModelRepository
import com.mobilityk.core.repository.CarTrimRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class CarManufacturerService(
    private val carManufacturerRepository: CarManufacturerRepository,
    private val carManufacturerMapper: CarManufacturerMapper,
    private val carModelRepository: CarModelRepository,
    private val carClassRepository: CarClassRepository,
    private val carTrimRepository: CarTrimRepository
) {
    @Transactional
    fun findById(id: Long): CarManufacturer {
        return carManufacturerRepository.findById(id).orElseThrow { CommException("not found carManufacturer") }
    }

    @Transactional
    fun findAllBy(pageable: Pageable): Page<CarManufacturerDTO> {
        return carManufacturerRepository.findAll(pageable)
            .map { carManufacturer ->
                carManufacturerMapper.toDto(carManufacturer)
            }
    }

    @Transactional
    fun findAllBySearchOption(searchOption: CarManufacturerSearchOption, pageable: Pageable): Page<CarManufacturerDTO> {
        return carManufacturerRepository.findAllBySearch(searchOption, pageable)
            .map { carManufacturer ->
                carManufacturerMapper.toDto(carManufacturer)
            }
    }

    @Transactional
    fun findAllBySearchOption(searchOption: CarManufacturerSearchOption): List<CarManufacturerDTO> {
        return carManufacturerRepository.findAllBySearch(searchOption)
            .map { carManufacturer ->
                carManufacturerMapper.toDto(carManufacturer)
            }
    }

    @Transactional
    fun findAll(): List<CarManufacturerDTO> {
        return carManufacturerMapper.toDtoList(carManufacturerRepository.findAll())
    }

    @Transactional
    fun findAllByCountryType(countryType: CountryType): List<CarManufacturerDTO> {
        val list = carManufacturerRepository.findAllByCountryType(countryType)
        return carManufacturerMapper.toDtoList(list.sortedBy { it.name })
    }

    @Transactional
    fun findAllByCountryTypeAndPublishedTrue(countryType: CountryType): List<CarManufacturerDTO> {
        return carManufacturerMapper.toDtoList(carManufacturerRepository.findAllByCountryTypeAndPublishedTrue(countryType))
    }

    @Transactional
    fun findAllByPublishedTrue(): List<CarManufacturerDTO> {
        return carManufacturerMapper.toDtoList(carManufacturerRepository.findAllByPublishedTrue())
    }

    @Transactional
    fun create(carManufacturerVM: CarManufacturerVM): CarManufacturerDTO {
        val carManufacturerOpt = carManufacturerRepository.findByName(carManufacturerVM.name!!)
        return if(carManufacturerOpt.isEmpty) {
            val carManufacturer = CarManufacturer()
            carManufacturer.create(carManufacturerVM)
            carManufacturerMapper.toDto(carManufacturerRepository.save(carManufacturer))
        } else {
            carManufacturerMapper.toDto(carManufacturerOpt.get())
        }
    }

    @Transactional
    fun update(id: Long, carManufacturerVM: CarManufacturerVM): CarManufacturerDTO {
        val carManufact = carManufacturerRepository.findById(id)
        if(carManufact.isEmpty) throw CommException("not found")
        carManufact.get().update(carManufacturerVM)
        return carManufacturerMapper.toDto(
            carManufacturerRepository.save(carManufact.get())
        )
    }

    @Transactional
    fun updateAll(carManufacturerAllVM: CarManufacturerAllVM): Unit {
        carManufacturerAllVM.deleteList?.let { list ->
            list.forEach { item ->
                val opt = carManufacturerRepository.findByName(item.name!!)
                if(opt.isPresent) {
                    carManufacturerRepository.delete(opt.get())
                }
            }
        }
        carManufacturerAllVM.addList?.let { list ->
            list.forEach { item ->
                carManufacturerRepository.save(
                    CarManufacturer(
                        name = item.name,
                        nameKr = item.nameKr,
                        nameEng = item.nameEng,
                        imgPath = "",
                        published = true,
                        countryType = item.countryType,
                        country = Country.GERMANY
                    )
                )
            }
        }
    }

    @Transactional
    fun updatePublish(id: Long, carManufacturerVM: CarManufacturerVM): CarManufacturerDTO{
        val carManufacturer = carManufacturerRepository.findById(id).orElseThrow { CommException("not found carManufacturer") }
        carManufacturer.published = carManufacturerVM.published
        return carManufacturerMapper.toDto(carManufacturerRepository.save(carManufacturer))
    }

    @Transactional
    fun delete(id: Long): CarManufacturerDTO {
        val carManufacturer = carManufacturerRepository.findById(id).orElseThrow { CommException("not found") }
        val carManufacturerDTO = carManufacturerMapper.toDto(carManufacturer)
        carManufacturerRepository.deleteById(carManufacturer.id!!)
        return carManufacturerDTO
    }

    @Transactional
    fun makeCarManufacturer() {
        var name = CarManufacturerName.AUDI.description
        var opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/audi.png"
                )
            )
        }

        name = CarManufacturerName.BENTLEY.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/bentley.png"
                )
            )
        }

        name = CarManufacturerName.BENZ.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "???????????????-??????",
                    imgPath = "/carimg/benz.png"
                )
            )
        }

        name = CarManufacturerName.BENZ_SMART.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????? ?????????",
                    imgPath = "/carimg/benz_smart.png"
                )
            )
        }

        name = CarManufacturerName.BMW.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "???????????????",
                    imgPath = "/carimg/bmw.png"
                )
            )
        }

        name = CarManufacturerName.BMW_MINI.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????????????? ??????",
                    imgPath = "/carimg/bmw-mini.png"
                )
            )
        }

        name = CarManufacturerName.CADILLAC.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/cadillac.png"
                )
            )
        }

        name = CarManufacturerName.CHEVROLET.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.DOMESTIC,
                    published = false,
                    country = Country.KOREA,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/chevrolet.png"
                )
            )
        }

        name = CarManufacturerName.CHRYSLER.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "???????????????",
                    imgPath = "/carimg/chrysler.png"
                )
            )
        }

        name = CarManufacturerName.CITROEN.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "????????????",
                    imgPath = "/carimg/citroen.png"
                )
            )
        }

        name = CarManufacturerName.FERRARI.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/ferrari.png"
                )
            )
        }

        name = CarManufacturerName.FIAT.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/fiat.png"
                )
            )
        }

        name = CarManufacturerName.FORD.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/ford.png"
                )
            )
        }

        name = CarManufacturerName.GENESIS.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.DOMESTIC,
                    published = false,
                    country = Country.KOREA,
                    nameEng = name,
                    nameKr = "????????????",
                    imgPath = "/carimg/genesis.png"
                )
            )
        }

        name = CarManufacturerName.HONDA.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.JAPAN,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/honda.png"
                )
            )
        }

        name = CarManufacturerName.HYUNDAI.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.DOMESTIC,
                    published = false,
                    country = Country.KOREA,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/hyundai.png"
                )
            )
        }

        name = CarManufacturerName.INFINITY.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "????????????",
                    imgPath = "/carimg/infinity.png"
                )
            )
        }

        name = CarManufacturerName.JAGUAR.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/jaguar.png"
                )
            )
        }

        name = CarManufacturerName.JEEP.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/jeep.png"
                )
            )
        }

        name = CarManufacturerName.KIA.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.DOMESTIC,
                    published = false,
                    country = Country.KOREA,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/kia.png"
                )
            )
        }

        name = CarManufacturerName.LANDROVER.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "????????????",
                    imgPath = "/carimg/landrover.png"
                )
            )
        }

        name = CarManufacturerName.LEXUS.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/lexus.png"
                )
            )
        }

        name = CarManufacturerName.LINCOLN.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/lincoln.png"
                )
            )
        }

        name = CarManufacturerName.MASERATI.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "????????????",
                    imgPath = "/carimg/maserati.png"
                )
            )
        }

        name = CarManufacturerName.MITSUBISHI.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "????????????",
                    imgPath = "/carimg/mitsubishi.png"
                )
            )
        }

        name = CarManufacturerName.NISSAN.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/nissan.png"
                )
            )
        }

        name = CarManufacturerName.PEUGEOT.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/peugeot.png"
                )
            )
        }

        name = CarManufacturerName.PORSCHE.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/porsche.png"
                )
            )
        }

        name = CarManufacturerName.RENAULT.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.DOMESTIC,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/renault.png"
                )
            )
        }

        name = CarManufacturerName.ROLLSROYS.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "???????????????",
                    imgPath = "/carimg/rollsroys.png"
                )
            )
        }

        name = CarManufacturerName.SSANGYONG.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.DOMESTIC,
                    published = false,
                    country = Country.KOREA,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/ssangyong.png"
                )
            )
        }

        name = CarManufacturerName.TESLA.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.USA,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/tesla.png"
                )
            )
        }

        name = CarManufacturerName.TOYOTA.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.JAPAN,
                    nameEng = name,
                    nameKr = "?????????",
                    imgPath = "/carimg/toyota.png"
                )
            )
        }

        name = CarManufacturerName.VOLKSWAGEN.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "????????????",
                    imgPath = "/carimg/volkswagen.png"
                )
            )
        }

        name = CarManufacturerName.VOLVO.description
        opt = carManufacturerRepository.findByName(name)
        if(opt.isEmpty) {
            carManufacturerRepository.save(
                CarManufacturer(
                    name = name,
                    countryType = CountryType.INTERNATIONAL,
                    published = false,
                    country = Country.GERMANY,
                    nameEng = name,
                    nameKr = "??????",
                    imgPath = "/carimg/volvo.png"
                )
            )
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createBaseBulk(
        carManufacturerName: CarManufacturerName,
        carModelName: String,
        carModelDetailName: String,
        carClassName: String,
        carDisplacement: Int,
        popularType: PopularType,
        carTrim: String
    ): CarTrim? {
        val carManufacturer = carManufacturerRepository.findByName(carManufacturerName.description).orElseThrow { CommException("not found carmanufacturer") }
        val carModelOpt = carModelRepository.findByCarManufacturerAndNameAndNameDetail(
            carManufacturer = carManufacturer,
            carModelName,
            carModelDetailName
        )
        var carModel: CarModel = if(carModelOpt.isPresent) {
            carModelOpt.get()
        } else {
            carModelRepository.save(
                CarModel(
                    carManufacturer = carManufacturer,
                    name = carModelName,
                    nameDetail = carModelDetailName,
                    published = true
                )
            )
        }
        val carClassOpt = carClassRepository.findByCarModelAndNameAndCarDisplacementAndPopularType(
            carModel = carModel,
            name = carClassName,
            carDisplacement = carDisplacement,
            popularType = popularType
        )
        val carClass: CarClass = if(carClassOpt.isPresent) {
            carClassOpt.get()
        } else {
            carClassRepository.save(
                CarClass(
                    carModel = carModel,
                    name = carClassName,
                    carDisplacement = carDisplacement,
                    published = true,
                    popularType = popularType
                )
            )
        }
        val carTrimOpt = carTrimRepository.findByCarClassAndName(carClass, carTrim)

        return if(carTrimOpt.isEmpty) {
            carTrimRepository.save(
                CarTrim(
                    carClass = carClass,
                    name = carTrim,
                    published = true
                )
            )
        } else {
            null
        }
    }
}