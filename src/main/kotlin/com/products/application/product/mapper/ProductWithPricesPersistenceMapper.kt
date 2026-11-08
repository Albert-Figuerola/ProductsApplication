package com.products.application.product.mapper

import com.products.application.price.mapper.toDomain
import com.products.application.product.entity.ProductEntity
import com.products.application.product.query.model.ProductWithPrices

fun ProductEntity.toProductWithPrices(): ProductWithPrices =
    ProductWithPrices(
        id = id,
        name = name,
        description = description,
        prices = prices
            .map { it.toDomain() }
    )