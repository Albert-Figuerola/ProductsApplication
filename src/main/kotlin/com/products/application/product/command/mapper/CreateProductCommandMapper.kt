package com.products.application.product.command.mapper

import com.products.application.product.command.model.CreateProductCommand
import com.products.application.product.command.model.CreateProductRequest

internal fun CreateProductRequest.toCommand() =
    CreateProductCommand(
        name = name,
        description = description,
    )