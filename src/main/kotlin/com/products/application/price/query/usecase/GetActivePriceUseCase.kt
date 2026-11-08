package com.products.application.price.query.usecase

import com.products.application.price.domain.Price
import com.products.application.price.port.PricePort
import com.products.application.product.exception.ProductNotFoundException
import com.products.application.product.port.ProductPort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class GetActivePriceUseCase(
    private val productPort: ProductPort,
    private val pricePort: PricePort
) {

    fun execute(productId: UUID, date: LocalDate): Price? {
        if (!productPort.existsById(productId)) {
            throw ProductNotFoundException(productId)
        }

        return pricePort.findActivePrice(productId, date)
    }

}