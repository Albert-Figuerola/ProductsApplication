package com.products.application.price.controller

import com.products.application.common.response.SuccessResponse
import com.products.application.common.response.success
import com.products.application.price.command.mapper.toCommand
import com.products.application.price.command.usecase.CreatePriceUseCase
import com.products.application.price.command.model.CreatePriceRequest
import com.products.application.price.mapper.toActivePriceResponse
import com.products.application.price.mapper.toResponse
import com.products.application.price.query.response.ActivePriceResponse
import com.products.application.price.query.usecase.GetActivePriceUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v1/products")
class PriceController(
    private val createPriceUseCase: CreatePriceUseCase,
    private val getActivePriceUseCase: GetActivePriceUseCase,
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

    @GetMapping("/{id}/prices", params = ["date"])
    fun getActivePrice(
        @PathVariable("id") productId: UUID,
        @RequestParam date: LocalDate
    ): ResponseEntity<ActivePriceResponse> =
        getActivePriceUseCase.execute(productId, date)
            ?.let { ResponseEntity.ok(it.toActivePriceResponse()) }
            ?: ResponseEntity.noContent().build()

}