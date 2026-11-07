package com.products.application.product.query.response

import java.util.UUID

data class ProductResponse(
    val id: UUID,
    val name: String,
    val description: String
)