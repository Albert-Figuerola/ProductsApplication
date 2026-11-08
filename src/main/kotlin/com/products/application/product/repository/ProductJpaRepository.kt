package com.products.application.product.repository

import com.products.application.product.entity.ProductEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ProductJpaRepository : JpaRepository<ProductEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :productId")
    fun findByIdForUpdate(productId: UUID): ProductEntity?

}