package com.products.application.product.mapper

import com.products.application.common.extension.roundToTwoDecimals
import com.products.application.product.query.model.ProductWithPrices
import com.products.application.product.query.response.PriceHistoryResponse
import com.products.application.product.query.response.ProductPriceHistoryResponse

internal fun ProductWithPrices.toResponse(): ProductPriceHistoryResponse =
    ProductPriceHistoryResponse(
        name = name,
        description = description,
        prices = prices.map { price ->
            PriceHistoryResponse(
                value = price.value.roundToTwoDecimals(),
                initDate = price.initDate,
                endDate = price.endDate
            )
        }
    )