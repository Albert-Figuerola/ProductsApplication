package com.products.application.price.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Price (
    val id: UUID,
    val value: BigDecimal,
    val initDate: LocalDate,
    val endDate: LocalDate?,
) {
    init {
        require(value > BigDecimal.ZERO) {
            "Price value must be positive"
        }

        require(endDate == null || endDate.isAfter(initDate)) {
            "End date must be after init date"
        }
    }
}