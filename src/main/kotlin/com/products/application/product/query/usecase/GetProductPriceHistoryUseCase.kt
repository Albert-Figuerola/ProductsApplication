package com.products.application.product.query.usecase

import com.products.application.product.exception.ProductNotFoundException
import com.products.application.product.port.ProductPort
import com.products.application.product.query.model.ProductWithPrices
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetProductPriceHistoryUseCase(
    private val productPort: ProductPort
) {

    fun execute(productId: UUID): ProductWithPrices =
        productPort.findByIdWithPrices(productId)
            ?: throw ProductNotFoundException(productId)

}