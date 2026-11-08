package com.products.application.price.controller

import com.products.application.common.response.SuccessResponse
import com.products.application.common.response.success
import com.products.application.price.command.mapper.toCommand
import com.products.application.price.command.usecase.CreatePriceUseCase
import com.products.application.price.command.model.CreatePriceRequest
import com.products.application.price.mapper.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/products")
class PriceController(
    private val createPriceUseCase: CreatePriceUseCase
) {

    @PostMapping("/{id}/prices")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPrice(
        @PathVariable("id") productId: UUID,
        @Valid @RequestBody request: CreatePriceRequest
    ): SuccessResponse =
        success(
            createPriceUseCase.execute(
                request.toCommand(), productId
            ).toResponse()
        )

}