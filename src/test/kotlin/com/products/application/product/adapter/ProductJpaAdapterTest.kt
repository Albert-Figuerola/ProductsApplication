package com.products.application.product.adapter

import com.products.application.price.adapter.PriceJpaAdapter
import com.products.application.price.domain.Price
import com.products.application.product.domain.Product
import com.products.application.product.repository.ProductJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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

@SpringBootTest
@Testcontainers
class ProductJpaAdapterTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmField
        val mysql = MySQLContainer("mysql:8.4.11")
    }

    @Autowired
    private lateinit var productJpaAdapter: ProductJpaAdapter

    @Autowired
    private lateinit var productJpaRepository: ProductJpaRepository

    @Autowired
    private lateinit var priceJpaAdapter: PriceJpaAdapter

    @Test
    fun `should persist a product successfully`() {
        val product = createProduct()

        productJpaAdapter.save(product)

        val persistedProduct = productJpaRepository.findById(product.id)
            .orElseThrow {
                AssertionError("Product was not persisted")
            }

        assertEquals(product.id, persistedProduct.id)
        assertEquals(product.name, persistedProduct.name)
        assertEquals(product.description, persistedProduct.description)
        assertTrue(persistedProduct.createdAt <= LocalDateTime.now())
        assertTrue(persistedProduct.updatedAt <= LocalDateTime.now())
    }

    @Test
    fun `should find product with prices ordered by init date`() {
        val productId = UUID.randomUUID()

        productJpaAdapter.save(
            Product(
                id = productId,
                name = "Zapatillas deportivas",
                description = "Modelo 2025 edición limitada"
            )
        )

        val newerPrice = Price(
            id = UUID.randomUUID(),
            value = BigDecimal("199.99"),
            initDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 6, 30)
        )

        val olderPrice = Price(
            id = UUID.randomUUID(),
            value = BigDecimal("99.99"),
            initDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 6, 30)
        )

        priceJpaAdapter.save(newerPrice, productId)
        priceJpaAdapter.save(olderPrice, productId)

        val result = requireNotNull(
            productJpaAdapter.findByIdWithPrices(productId)
        )

        assertEquals(productId, result.id)
        assertEquals("Zapatillas deportivas", result.name)
        assertEquals("Modelo 2025 edición limitada", result.description)

        assertEquals(2, result.prices.size)
        assertEquals(olderPrice.id, result.prices[0].id)
        assertEquals(newerPrice.id, result.prices[1].id)
    }

    @Test
    fun `should find product with empty prices`() {
        val productId = UUID.randomUUID()

        productJpaAdapter.save(
            Product(
                id = productId,
                name = "Test product",
                description = "Test description"
            )
        )

        val result = requireNotNull(
            productJpaAdapter.findByIdWithPrices(productId)
        )

        assertEquals(productId, result.id)
        assertTrue(result.prices.isEmpty())
    }

    @Test
    fun `should return null when product with prices does not exist`() {
        val productId = UUID.randomUUID()

        val result = productJpaAdapter.findByIdWithPrices(productId)

        assertNull(result)
    }

    private fun createProduct() =
        Product(
            id = UUID.randomUUID(),
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada"
        )

}