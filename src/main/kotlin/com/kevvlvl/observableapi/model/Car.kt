package com.kevvlvl.observableapi.model

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "car")
class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false, length = 100)
    lateinit var name: String

    @Column(nullable = false, length = 100)
    lateinit var msrp: BigDecimal

    @Column(nullable = true, length = 100)
    var inventory: Int = 0


}