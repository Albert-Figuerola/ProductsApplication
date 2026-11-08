package com.products.application.price.mapper

import com.products.application.price.domain.Price
import com.products.application.price.entity.PriceEntity

internal fun PriceEntity.toDomain() =
    Price(
        id = id,
        value = value,
        initDate = initDate,
        endDate = endDate
    )