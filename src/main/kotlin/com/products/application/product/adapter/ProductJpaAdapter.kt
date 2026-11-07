package com.products.application.product.adapter

import com.products.application.product.domain.Product
import com.products.application.product.mapper.toDomain
import com.products.application.product.mapper.toEntity
import com.products.application.product.port.ProductPort
import com.products.application.product.repository.ProductJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ProductJpaAdapter(
    private val productJpaRepository: ProductJpaRepository
) : ProductPort {

    override fun save(product: Product): Product {
        return productJpaRepository.save(product.toEntity()).toDomain()
    }

}