package com.products.application.common.handler

import com.products.application.common.response.ErrorResponse
import com.products.application.price.exception.PriceDateOverlapException
import com.products.application.product.exception.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleProductNotFound(
        exception: ProductNotFoundException
    ): ErrorResponse = ErrorResponse(
        code = exception.code,
        message = exception.message
    )

    @ExceptionHandler(PriceDateOverlapException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handlePriceDateOverlap(
        exception: PriceDateOverlapException
    ): ErrorResponse = ErrorResponse(
        code = exception.code,
        message = exception.message
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(
        exception: MethodArgumentNotValidException
    ): ErrorResponse = ErrorResponse(
        code = "VALIDATION_ERROR",
        message = exception.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
    )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(): ErrorResponse =
        ErrorResponse(
            code = "INVALID_REQUEST_BODY",
            message = "Request body is missing required fields or contains invalid values"
        )

}