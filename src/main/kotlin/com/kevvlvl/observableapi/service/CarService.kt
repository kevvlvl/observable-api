package com.kevvlvl.observableapi.service

import com.kevvlvl.observableapi.model.Car
import com.kevvlvl.observableapi.repository.CarRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.Tracer
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class CarService @Autowired constructor(
    private val carRepository: CarRepository,
    private val trace: Tracer) {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun getAllCars(): Flux<Car> {

//        val svcSpan: Span = trace.nextSpan().name("car-svc-getAllCars")

        val svcSpan = this.trace.startScopedSpan("car-svc-getAllCars")
//        this.trace.withSpan(svcSpan)
//        svcSpan.start()

        logger.debug("getAllCars() START - About to call CarRepository")

        val carsEntities: List<Car> = carRepository.findAll()

        if(carsEntities.isNotEmpty()) {

            logger.info("Number of cars returned from DB {}", carsEntities.size)
            logger.debug("Cars entities returned:")
            carsEntities.forEach { c -> logger.debug("   Car = {}", c) }
            svcSpan.tag("getAllCars-count", carsEntities.size.toString())
        } else {
            logger.warn("No cars returned from the DB")
        }

        logger.debug("getAllCars() END - Return Iterable carsEntities")

        svcSpan.end()

        return Flux.fromIterable(carsEntities)
    }
}