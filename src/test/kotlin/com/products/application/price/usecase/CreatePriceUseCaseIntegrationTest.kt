package com.products.application.price.usecase

import com.products.application.price.command.model.CreatePriceCommand
import com.products.application.price.command.usecase.CreatePriceUseCase
import com.products.application.price.domain.Price
import com.products.application.price.exception.PriceDateOverlapException
import com.products.application.price.repository.PriceJpaRepository
import com.products.application.product.adapter.ProductJpaAdapter
import com.products.application.product.domain.Product
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mysql.MySQLContainer
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
@Testcontainers
class CreatePriceUseCaseIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmField
        val mysql = MySQLContainer("mysql:8.4.11")
    }

    @Autowired
    private lateinit var createPriceUseCase: CreatePriceUseCase

    @Autowired
    private lateinit var productJpaAdapter: ProductJpaAdapter

    @Autowired
    private lateinit var priceJpaRepository: PriceJpaRepository

    @Test
    fun `should prevent overlapping prices from concurrent requests`() {
        val product = createProduct()
        val persistedProduct = productJpaAdapter.save(product)

        val firstCommand = createFirstCommand()
        val secondCommand = createSecondCommand()

        val executor = Executors.newFixedThreadPool(2)
        val startLatch = CountDownLatch(1)

        try {
            val firstFuture = executor.submit<Price> {
                startLatch.await()
                createPriceUseCase.execute(firstCommand, persistedProduct.id)
            }

            val secondFuture = executor.submit<Price> {
                startLatch.await()
                createPriceUseCase.execute(secondCommand, persistedProduct.id)
            }

            startLatch.countDown()

            val results = listOf(firstFuture, secondFuture).map { future ->
                runCatching {
                    future.get(10, TimeUnit.SECONDS)
                }
            }

            assertEquals(1, results.count { it.isSuccess })

            assertEquals(
                1,
                results.count {
                    it.exceptionOrNull() is ExecutionException
                            && it.exceptionOrNull()?.cause is PriceDateOverlapException
                }
            )

            val persistedPrices = priceJpaRepository.findAll()
                .filter { it.product.id == persistedProduct.id }

            assertEquals(1, persistedPrices.size)

            val persistedPrice = persistedPrices.single()

            val successfulPrice = results
                .single { it.isSuccess }
                .getOrThrow()

            assertEquals(successfulPrice.id, persistedPrice.id)
        } finally {
            executor.shutdownNow()
        }

    }

    private fun createProduct() =
        Product(
            id = UUID.randomUUID(),
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada"
        )

    private fun createFirstCommand() =
        CreatePriceCommand(
            value = BigDecimal("99.99"),
            initDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 6, 30)
        )

    private fun createSecondCommand() =
        CreatePriceCommand(
            value = BigDecimal("119.99"),
            initDate = LocalDate.of(2024, 3, 1),
            endDate = LocalDate.of(2024, 9, 30)
        )
}