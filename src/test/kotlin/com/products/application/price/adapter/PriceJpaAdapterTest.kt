package com.products.application.price.adapter

import com.products.application.price.domain.Price
import com.products.application.price.repository.PriceJpaRepository
import com.products.application.product.adapter.ProductJpaAdapter
import com.products.application.product.domain.Product
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mysql.MySQLContainer
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest
@Testcontainers
class PriceJpaAdapterTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmField
        val mysql = MySQLContainer("mysql:8.4.11")
    }

    @Autowired
    private lateinit var priceJpaAdapter: PriceJpaAdapter

    @Autowired
    private lateinit var priceJpaRepository: PriceJpaRepository

    @Autowired
    private lateinit var productJpaAdapter: ProductJpaAdapter


    @Test
    fun `should persist a price for an existing product`() {
        val product = createProduct()
        val persistedProduct = productJpaAdapter.save(product)

        val price = createPrice()
        priceJpaAdapter.save(price, persistedProduct.id)

        val persistedPrice = priceJpaRepository.findById(price.id)
            .orElseThrow {
                AssertionError("Price was not persisted")
            }

        assertEquals(price.id, persistedPrice.id)
        assertEquals(0, price.value.compareTo(persistedPrice.value))
        assertEquals(price.initDate, persistedPrice.initDate)
        assertEquals(price.endDate, persistedPrice.endDate)
        assertEquals(persistedProduct.id, persistedPrice.product.id)

        assertTrue(persistedPrice.createdAt <= LocalDateTime.now())
        assertTrue(persistedPrice.updatedAt <= LocalDateTime.now())
    }

    private fun createProduct() =
        Product(
            id = UUID.randomUUID(),
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada"
        )

    private fun createPrice() =
        Price(
            id = UUID.randomUUID(),
            value = BigDecimal("99.123456"),
            initDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 2, 1),
        )

}