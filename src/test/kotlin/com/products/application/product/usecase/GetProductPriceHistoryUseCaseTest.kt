package com.products.application.product.usecase

import com.products.application.price.domain.Price
import com.products.application.product.exception.ProductNotFoundException
import com.products.application.product.port.ProductPort
import com.products.application.product.query.model.ProductWithPrices
import com.products.application.product.query.usecase.GetProductPriceHistoryUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class GetProductPriceHistoryUseCaseTest {

    private val productPort: ProductPort = mock()

    private val useCase = GetProductPriceHistoryUseCase(productPort)

    @Test
    fun `should return product with price history`() {
        val productId = UUID.randomUUID()

        val productWithPrices = ProductWithPrices(
            id = productId,
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada",
            prices = listOf(
                Price(
                    id = UUID.randomUUID(),
                    value = BigDecimal("99.99"),
                    initDate = LocalDate.of(2024, 1, 1),
                    endDate = LocalDate.of(2024, 6, 30)
                ),
                Price(
                    id = UUID.randomUUID(),
                    value = BigDecimal("199.99"),
                    initDate = LocalDate.of(2025, 1, 1),
                    endDate = LocalDate.of(2025, 6, 30)
                )
            )
        )

        whenever(productPort.findByIdWithPrices(productId))
            .thenReturn(productWithPrices)

        val result = useCase.execute(productId)

        assertEquals(productWithPrices, result)
        verify(productPort).findByIdWithPrices(productId)
    }

    @Test
    fun `should return product with empty price history`() {
        val productId = UUID.randomUUID()

        val productWithPrices = ProductWithPrices(
            id = productId,
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada",
            prices = emptyList()
        )

        whenever(productPort.findByIdWithPrices(productId))
            .thenReturn(productWithPrices)

        val result = useCase.execute(productId)

        assertEquals(productWithPrices, result)
        assertTrue(result.prices.isEmpty())
        verify(productPort).findByIdWithPrices(productId)
    }

    @Test
    fun `should throw ProductNotFoundException when product does not exist`() {
        val productId = UUID.randomUUID()

        whenever(productPort.findByIdWithPrices(productId))
            .thenReturn(null)

        assertThrows<ProductNotFoundException> {
            useCase.execute(productId)
        }

        verify(productPort).findByIdWithPrices(productId)
    }

}