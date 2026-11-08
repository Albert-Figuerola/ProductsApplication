package com.products.application.product.query.response

data class ProductPriceHistoryResponse(
    val name: String,
    val description: String,
    val prices: List<PriceHistoryResponse>
)