package com.products.application.product.mapper

import com.products.application.product.domain.Product
import com.products.application.product.query.response.ProductResponse

internal fun Product.toResponse() =
    ProductResponse(
        id = id,
        name = name,
        description = description
    )