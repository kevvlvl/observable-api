package com.kevvlvl.observableapi.service

import com.kevvlvl.observableapi.delivery.CarDelivery
import com.kevvlvl.observableapi.dto.CarDto
import com.kevvlvl.observableapi.model.Car
import com.kevvlvl.observableapi.repository.CarRepository
import io.opentracing.Tracer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class CarService @Autowired constructor(
    private val carRepository: CarRepository,
    private val carDelivery: CarDelivery,
    private val trace: Tracer
) {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun getAllCars(): Flux<CarDto> {

        val svcSpan = this.trace.buildSpan("car-svc-getAllCars").start()

        logger.debug("getAllCars() START - About to call CarRepository")

        val carsEntities: List<Car> = carRepository.findAll()
        var carsDtos: List<CarDto> = ArrayList()

        if(carsEntities.isNotEmpty()) {

            logger.info("   Number of cars returned from DB {}", carsEntities.size)
            logger.debug("   Cars entities returned:")
            carsEntities.forEach { c -> logger.debug("   Car = {}", c) }
            svcSpan.setTag("getAllCars-count", carsEntities.size.toString())

            logger.debug("   Obtained carsEntities. Prepare entities into DTO")
            carsDtos = carDelivery.getPreparedData(carsEntities);

        } else {
            logger.warn("No cars returned from the DB")
        }

        logger.debug("getAllCars() END - Return DTOs")
        svcSpan.finish()

        return Flux.fromIterable(carsDtos)
    }
}