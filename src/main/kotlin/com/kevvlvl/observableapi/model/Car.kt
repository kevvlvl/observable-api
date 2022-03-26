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

    override fun toString(): String {
        return "Car(id=$id, name='$name', msrp=$msrp, inventory=$inventory)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Car

        if (id != other.id) return false
        if (name != other.name) return false
        if (msrp != other.msrp) return false
        if (inventory != other.inventory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + msrp.hashCode()
        result = 31 * result + inventory
        return result
    }
}