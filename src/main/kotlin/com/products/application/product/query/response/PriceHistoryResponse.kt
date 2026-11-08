package com.products.application.product.query.response

import java.math.BigDecimal
import java.time.LocalDate

data class PriceHistoryResponse(
    val value: BigDecimal,
    val initDate: LocalDate,
    val endDate: LocalDate?
)
