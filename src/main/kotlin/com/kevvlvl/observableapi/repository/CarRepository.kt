package com.kevvlvl.observableapi.repository

import com.kevvlvl.observableapi.model.Car
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarRepository: JpaRepository<Car, Long> {
}