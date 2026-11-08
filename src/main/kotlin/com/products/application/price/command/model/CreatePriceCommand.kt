package com.products.application.price.command.model

import java.math.BigDecimal
import java.time.LocalDate

data class CreatePriceCommand(
    val value: BigDecimal,
    val initDate: LocalDate,
    val endDate: LocalDate?
)