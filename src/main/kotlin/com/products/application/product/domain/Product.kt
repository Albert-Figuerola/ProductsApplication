package com.products.application.product.domain

import java.util.UUID

data class Product (
    val id: UUID,
    val name: String,
    val description: String
)