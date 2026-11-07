package com.products.application.product.controller

import com.products.application.common.response.SuccessResponse
import com.products.application.common.response.success
import com.products.application.product.command.mapper.toCommand
import com.products.application.product.command.model.CreateProductRequest
import com.products.application.product.command.usecase.CreateProductUseCase
import com.products.application.product.mapper.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val createProductUseCase: CreateProductUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest
    ): SuccessResponse =
        success(
            createProductUseCase.execute(request.toCommand()).toResponse(),
        )

}