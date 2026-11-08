package com.products.application.price.usecase

import com.products.application.price.domain.Price
import com.products.application.price.port.PricePort
import com.products.application.price.query.usecase.GetActivePriceUseCase
import com.products.application.product.exception.ProductNotFoundException
import com.products.application.product.port.ProductPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class GetActivePriceUseCaseTest {

    private val productPort: ProductPort = mock()
    private val pricePort: PricePort = mock()

    private val useCase = GetActivePriceUseCase(productPort, pricePort)

    @Test
    fun `should return active price for given date`() {
        val productId = UUID.randomUUID()
        val date = LocalDate.of(2024, 4, 15)
        val price = createPrice()

        whenever(productPort.existsById(productId)).thenReturn(true)
        whenever(pricePort.findActivePrice(productId, date)).thenReturn(price)

        val result = useCase.execute(productId, date)

        assertEquals(price, result)
        verify(productPort).existsById(productId)
        verify(pricePort).findActivePrice(productId, date)
    }

    @Test
    fun `should return null when no active price exists`() {
        val productId = UUID.randomUUID()
        val date = LocalDate.of(2024, 4, 15)

        whenever(productPort.existsById(productId)).thenReturn(true)
        whenever(pricePort.findActivePrice(productId, date)).thenReturn(null)

        val result = useCase.execute(productId, date)

        assertNull(result)
        verify(productPort).existsById(productId)
        verify(pricePort).findActivePrice(productId, date)
    }

    @Test
    fun `should throw ProductNotFoundException when product does not exist`() {
        val productId = UUID.randomUUID()
        val date = LocalDate.of(2024, 4, 15)

        whenever(productPort.existsById(productId)).thenReturn(false)

        assertThrows<ProductNotFoundException> {
            useCase.execute(productId, date)
        }

        verify(productPort).existsById(productId)
        verifyNoInteractions(pricePort)
    }

    private fun createPrice() =
        Price(
            id = UUID.randomUUID(),
            value = BigDecimal("99.99"),
            initDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

}