package com.products.application.price.command.model

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreatePriceRequest(
    @field:DecimalMin(value = "0.0", inclusive = false)
    val value: BigDecimal,

    @field:NotNull
    val initDate: LocalDate,

    val endDate: LocalDate?
)