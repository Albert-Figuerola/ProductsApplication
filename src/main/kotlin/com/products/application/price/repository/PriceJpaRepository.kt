package com.products.application.price.repository

import com.products.application.price.entity.PriceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.UUID

interface PriceJpaRepository : JpaRepository<PriceEntity, UUID> {

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM PriceEntity p
        WHERE p.product.id = :productId
        AND (:endDate IS NULL OR p.initDate <= :endDate)
        AND (p.endDate IS NULL OR p.endDate >= :initDate)
    """
    )
    fun existsOverlappingPrice(
        productId: UUID,
        initDate: LocalDate,
        endDate: LocalDate?
    ): Boolean

}