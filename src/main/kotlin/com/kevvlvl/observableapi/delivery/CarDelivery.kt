package com.kevvlvl.observableapi.delivery

import com.kevvlvl.observableapi.dto.CarDto
import com.kevvlvl.observableapi.model.Car
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CarDelivery @Autowired constructor() {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }


    fun getPreparedData(carsEntities: List<Car>): List<CarDto> {

        val carsDtos = ArrayList<CarDto>()
        logger.debug("getPreparedData() START - Prepare entities into DTOs")
        logger.info("BUSINESS RULE CD01 - Begin reviewing cars inventory")

        if(carsEntities.isNotEmpty()) {

            carsEntities.forEach { e ->

                carsDtos.add(CarDto(
                    e.id,
                    e.inventory,
                    e.msrp,
                    e.name,
                ))
            }
        }

        logger.info("BUSINESS RULE CD01 - Reviewed")
        logger.info("BUSINESS RULE CD02 - Begin adding supplementary info")

        // TODO: add additional info for each DTO

        carsDtos.forEach { c->
            c.inventoryReadyForDelivery = 10 // we decide only 10 cars are ready for customer delivery
            c.dealerSurplusMax = BigDecimal("5000.99")
            c.deliveryComments = "Perform inspection and exterior wash"
        }

        logger.info("BUSINESS RULE CD02 - Done adding supplementary info")

        logger.debug("getPreparedData() END - Return carsDtos. Count = {}", carsDtos.size)
        return carsDtos
    }
}