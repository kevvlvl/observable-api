package com.kevvlvl.observableapi.handler

import com.kevvlvl.observableapi.audit.AuditRequest
import com.kevvlvl.observableapi.dto.CarDto
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

        AuditRequest.audit(request)

        logger.info("About to query for all cars from the DB")
        logger.debug("cars() START - About to call CarService")

        val cars = carService.getAllCars()

        logger.debug("cars() END - CarService called")
        logger.info("Done querying for all cars.")

        return ServerResponse
            .ok()
            .body(cars)
    }

    fun reserve(request: ServerRequest): Mono<ServerResponse> {

        AuditRequest.audit(request)

        // mock endpoint always returns HTTP 500 to showcase metrics
        return ServerResponse
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build()
    }
}