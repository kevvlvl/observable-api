package com.kevvlvl.observableapi.route

import com.kevvlvl.observableapi.handler.CarsHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@Configuration(proxyBeanMethods = false)
class ApiRoute {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Bean
    fun route(carHandler: CarsHandler): RouterFunction<ServerResponse> {

        logger.info("Initialised routes")

        return route(GET("/cars")) {
            ok().body(carHandler.cars(it))
        }
    }
}