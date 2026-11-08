package com.products.application.product.adapter

import com.products.application.price.repository.PriceJpaRepository
import com.products.application.product.domain.Product
import com.products.application.product.exception.ProductNotFoundException
import com.products.application.product.mapper.toDomain
import com.products.application.product.mapper.toEntity
import com.products.application.product.port.ProductPort
import com.products.application.product.repository.ProductJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class ProductJpaAdapter(
    private val productJpaRepository: ProductJpaRepository
) : ProductPort {

    override fun save(product: Product): Product =
        productJpaRepository.save(product.toEntity()).toDomain()

    override fun lockById(productId: UUID) {
        productJpaRepository.findByIdForUpdate(productId)
            ?: throw ProductNotFoundException(productId)
    }

}