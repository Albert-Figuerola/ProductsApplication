package com.products.application.product.adapter

import com.products.application.product.domain.Product
import com.products.application.product.repository.ProductJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mysql.MySQLContainer
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

    @Test
    fun `should persist a product successfully`() {
        val product = createProduct()

        productJpaAdapter.save(product)

        val persistedProduct = productJpaRepository.findById(product.id)

        assertTrue(persistedProduct.isPresent)

        val entity = persistedProduct.orElseThrow {
            AssertionError("Product was not persisted")
        }

        assertEquals(product.id, entity.id)
        assertEquals(product.name, entity.name)
        assertEquals(product.description, entity.description)
        assertTrue(entity.createdAt <= LocalDateTime.now())
        assertTrue(entity.updatedAt <= LocalDateTime.now())
    }

    private fun createProduct() =
        Product(
            id = UUID.randomUUID(),
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada"
        )

}