package com.kevvlvl.observableapi.delivery

import com.kevvlvl.observableapi.dto.CarDto
import com.kevvlvl.observableapi.model.Car
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CarDelivery @Autowired constructor(
    meterRegistry: MeterRegistry
) {

    private lateinit var deliveryCounter: Counter

    init {

        deliveryCounter = Counter.builder("cd02-delivery")
            .tag("businessrule", "cd02")
            .description("The number of occurrences a request for inventory has successfully been enriched with delivery information")
            .register(meterRegistry)
    }

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

        carsDtos.forEach { c->
            c.inventoryReadyForDelivery = 10 // we decide only 10 cars are ready for customer delivery
            c.dealerSurplusMax = BigDecimal("5000.99")
            c.deliveryComments = "Perform inspection and exterior wash"
        }

        logger.info("BUSINESS RULE CD02 - Done adding supplementary info")

        deliveryCounter.increment()

        logger.debug("getPreparedData() END - Return carsDtos. Count = ${carsDtos.size}")
        return carsDtos
    }
}