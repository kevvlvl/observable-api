package com.kevvlvl.observableapi.handler

import com.kevvlvl.observableapi.service.CarService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CarsHandler @Autowired constructor(
    private val carService: CarService) {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun cars(request: ServerRequest): Mono<ServerResponse> {

        logger.info("Queried for the complete inventory of cars to be fetched from the database")

        val cars = carService.getAllCars()

        logger.info("Fetched all cars.")

        return ServerResponse
            .ok()
            .body(Flux.fromIterable(cars))
    }

    fun reserve(request: ServerRequest): Mono<ServerResponse> {

        logger.info("Received a request to reserve cars prior to purchase")
        logger.debug("reserve() START - About to call carService")

        // mock endpoint always returns HTTP 500 to showcase HTTP5xx metrics
        return ServerResponse
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build()
    }
}