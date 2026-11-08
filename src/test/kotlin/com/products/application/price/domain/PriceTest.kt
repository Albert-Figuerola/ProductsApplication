package com.products.application.price.domain

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class PriceTest {

    @Test
    fun `should create a valid price`() {
        assertDoesNotThrow {
            createPrice()
        }
    }

    @Test
    fun `should create a price without end date`() {
        assertDoesNotThrow {
            createPrice(endDate = null)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["0", "-1", "-99.99"])
    fun `should reject non-positive price values`(value: String) {
        assertThrows<IllegalArgumentException> {
            createPrice(value = BigDecimal(value))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["2024-01-01", "2023-12-31"])
    fun `should reject end date that is not after init date`(endDate: String) {
        assertThrows<IllegalArgumentException> {
            createPrice(endDate = LocalDate.parse(endDate))
        }
    }

    private fun createPrice(
        value: BigDecimal = BigDecimal("99.99"),
        endDate: LocalDate? = LocalDate.of(2024, 6, 30)
    ) = Price(
        id = UUID.randomUUID(),
        value = value,
        initDate = LocalDate.of(2024, 1, 1),
        endDate = endDate
    )

}