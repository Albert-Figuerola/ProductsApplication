package com.products.application.common.extension

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.roundToTwoDecimals(): BigDecimal =
    this.setScale(2, RoundingMode.HALF_UP)