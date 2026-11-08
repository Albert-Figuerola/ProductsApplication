package com.products.application.product.port

import com.products.application.product.domain.Product
import java.util.UUID

interface ProductPort {

    fun save(product: Product): Product

    fun lockById(productId: UUID)

    fun existsById(productId: UUID): Boolean
}