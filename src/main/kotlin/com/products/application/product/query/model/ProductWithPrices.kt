package com.products.application.product.query.model

import com.products.application.price.domain.Price
import java.util.UUID

data class ProductWithPrices(
    val id: UUID,
    val name: String,
    val description: String,
    val prices: List<Price>
)