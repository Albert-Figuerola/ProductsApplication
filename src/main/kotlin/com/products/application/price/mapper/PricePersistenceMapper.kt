package com.products.application.price.mapper

import com.products.application.price.domain.Price
import com.products.application.price.entity.PriceEntity
import com.products.application.product.entity.ProductEntity

internal fun Price.toEntity(product: ProductEntity) =
    PriceEntity(
        id = id,
        value = value,
        initDate = initDate,
        endDate = endDate,
        product = product
    )