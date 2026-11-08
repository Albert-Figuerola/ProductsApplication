package com.products.application.shared.extension

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.roundToTwoDecimals(): BigDecimal =
    this.setScale(2, RoundingMode.HALF_UP)