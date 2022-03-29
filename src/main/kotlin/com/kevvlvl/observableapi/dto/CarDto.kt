package com.kevvlvl.observableapi.dto

import java.math.BigDecimal

data class CarDto(
    val id: Long,
    val inventory: Int,
    val msrp: BigDecimal,
    val name: String,
    var inventoryReadyForDelivery: Int = 0,
    var dealerSurplusMax: BigDecimal = BigDecimal("0"),
    var deliveryComments: String = ""
)