package com.kevvlvl.observableapi.service

import com.kevvlvl.observableapi.aspect.LogFlow
import com.kevvlvl.observableapi.delivery.CarDelivery
import com.kevvlvl.observableapi.dto.CarDto
import com.kevvlvl.observableapi.model.Car
import com.kevvlvl.observableapi.repository.CarRepository
import io.opentracing.Tracer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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

    @LogFlow
    fun getAllCars(): List<CarDto> {

        logger.debug("Create a new trace span")
        val svcSpan = this.trace.buildSpan("car-svc-getAllCars").start()

        logger.debug("About to call carRepository")
        val carsEntities: List<Car> = carRepository.findAll()
        var carsDtos: List<CarDto> = ArrayList()

        if(carsEntities.isNotEmpty()) {

            val carsCount: String = carsEntities.size.toString()
            logger.info("Cars found in database $carsCount")

            carsEntities.forEach { c -> logger.debug("   Car = $c") }

            svcSpan.setTag("getAllCars-count", carsCount)

            logger.debug("carsEntities returned. Map entities to DTOs and apply business rules")
            carsDtos = carDelivery.getPreparedData(carsEntities);

            logger.debug("carsDtos mapped.")
            carsDtos.forEach { c -> logger.debug("   CarDto = $c") }

        } else {
            logger.warn("No cars found in the database. This could be normal. Try querying the table Car directly from an RDBMS client")
        }

        logger.debug("Finish the newly created trace span")
        svcSpan.finish()

        return carsDtos
    }
}