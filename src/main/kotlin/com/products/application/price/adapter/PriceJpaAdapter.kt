package com.products.application.price.adapter

import com.products.application.price.domain.Price
import com.products.application.price.mapper.toDomain
import com.products.application.price.mapper.toEntity
import com.products.application.price.port.PricePort
import com.products.application.price.repository.PriceJpaRepository
import com.products.application.product.exception.ProductNotFoundException
import com.products.application.product.repository.ProductJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class PriceJpaAdapter (
    private val priceJpaRepository: PriceJpaRepository,
    private val productJpaRepository: ProductJpaRepository
) : PricePort {

    override fun existsOverlappingPrice(
        productId: UUID,
        initDate: LocalDate,
        endDate: LocalDate?
    ): Boolean =
        priceJpaRepository.existsOverlappingPrice(
            productId = productId,
            initDate = initDate,
            endDate = endDate
        )

    override fun save(
        price: Price,
        productId: UUID
    ): Price {
        val productEntity = productJpaRepository.findByIdOrNull(productId)
            ?: throw ProductNotFoundException(productId)

        return priceJpaRepository.save(price.toEntity(productEntity)).toDomain()
    }

}