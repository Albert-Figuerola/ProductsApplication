package com.products.application.price.mapper

import com.products.application.common.extension.roundToTwoDecimals
import com.products.application.price.domain.Price
import com.products.application.price.query.response.ActivePriceResponse

internal fun Price.toActivePriceResponse() =
    ActivePriceResponse(
        value = value.roundToTwoDecimals()
    )