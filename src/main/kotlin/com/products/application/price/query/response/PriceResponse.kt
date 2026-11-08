package com.products.application.price.query.response

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class PriceResponse (
    val id: UUID,
    val value: BigDecimal,
    val initDate: LocalDate,
    val endDate: LocalDate?
)