package com.products.application.price.mapper

import com.products.application.price.domain.Price
import com.products.application.price.query.response.PriceResponse
import com.products.application.shared.extension.roundToTwoDecimals

internal fun Price.toResponse() =
    PriceResponse(
        id = id,
        value = value.roundToTwoDecimals(),
        initDate = initDate,
        endDate = endDate
    )