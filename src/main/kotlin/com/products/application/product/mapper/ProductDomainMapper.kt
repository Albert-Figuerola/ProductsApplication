package com.products.application.product.mapper

import com.products.application.product.domain.Product
import com.products.application.product.entity.ProductEntity

internal fun ProductEntity.toDomain() =
    Product(
        id = id,
        name = name,
        description = description
    )