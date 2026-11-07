package com.products.application.product.repository

import com.products.application.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProductJpaRepository : JpaRepository<ProductEntity, UUID> {
}