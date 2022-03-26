package com.kevvlvl.observableapi.service

import com.kevvlvl.observableapi.model.Car
import com.kevvlvl.observableapi.repository.CarRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class CarService @Autowired constructor(private val carRepository: CarRepository) {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun getAllCars(): Flux<Car> {

        logger.debug("getAllCars() START - About to call CarRepository")

        val carsEntities = carRepository.findAll()

        logger.debug("getAllCars() END - CarRepository returned {} cars", carsEntities.size)

        return Flux.fromIterable(carsEntities)
    }
}