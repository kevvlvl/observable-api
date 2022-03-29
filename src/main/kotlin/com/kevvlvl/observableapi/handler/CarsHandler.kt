package com.kevvlvl.observableapi.handler

import com.kevvlvl.observableapi.audit.AuditRequest
import com.kevvlvl.observableapi.model.Car
import com.kevvlvl.observableapi.service.CarService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Flux

@Component
class CarsHandler @Autowired constructor(
    private val carService: CarService) {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun cars(request: ServerRequest): Flux<Car> {

        AuditRequest.audit(request)

        logger.info("About to query for all cars from the DB")
        logger.debug("cars() START - About to call CarService")

        val cars = carService.getAllCars()

        logger.debug("cars() END - CarService called")
        logger.info("Done querying for all cars.")

        return cars
    }
}