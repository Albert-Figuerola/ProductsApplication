package com.products.application.common.response

typealias SuccessResponse = SuccessBody<Any>

data class SuccessBody<T>(
    val data: T
)

fun success(data: Any): SuccessResponse =
    SuccessBody(data)