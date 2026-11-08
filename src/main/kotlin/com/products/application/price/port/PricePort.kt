package com.products.application.price.port

import com.products.application.price.domain.Price
import java.time.LocalDate
import java.util.UUID

interface PricePort {

    fun existsOverlappingPrice(
        productId: UUID,
        initDate: LocalDate,
        endDate: LocalDate?
    ): Boolean

    fun save(price: Price, productId: UUID): Price

    fun findActivePrice(productId: UUID, date: LocalDate): Price?

}