package com.products.application.product.command.model

import jakarta.validation.constraints.NotBlank

data class CreateProductRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String,

    @field:NotBlank(message = "Description cannot be empty")
    val description: String,
)
