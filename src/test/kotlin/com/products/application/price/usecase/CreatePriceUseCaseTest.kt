package com.products.application.price.usecase

import com.products.application.price.command.model.CreatePriceCommand
import com.products.application.price.command.usecase.CreatePriceUseCase
import com.products.application.price.domain.Price
import com.products.application.price.exception.PriceDateOverlapException
import com.products.application.price.port.PricePort
import com.products.application.product.port.ProductPort
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.test.assertEquals

class CreatePriceUseCaseTest {

    private val productPort: ProductPort = mock()
    private val pricePort: PricePort = mock()
    private val createPriceUseCase = CreatePriceUseCase(productPort, pricePort)

    @Test
    fun `should create a price when dates do not overlap`() {
        val command = createPriceCommand()
        val productId = UUID.randomUUID()

        whenever(
            pricePort.existsOverlappingPrice(
                productId,
                command.initDate,
                command.endDate,
            )
        ).thenReturn(false)

        whenever(pricePort.save(any<Price>(), eq(productId)))
            .thenAnswer { it.getArgument<Price>(0) }

        val result = createPriceUseCase.execute(command, productId)

        assertEquals(command.value, result.value)
        assertEquals(command.initDate, result.initDate)
        assertEquals(command.endDate, result.endDate)

        verify(productPort).lockById(productId)
        verify(pricePort).existsOverlappingPrice(
            productId,
            command.initDate,
            command.endDate
        )
        verify(pricePort).save(result, productId)
    }

    @Test
    fun `should reject price creation when dates overlap`() {
        val command = createPriceCommand()
        val productId = UUID.randomUUID()

        whenever(
            pricePort.existsOverlappingPrice(
                productId,
                command.initDate,
                command.endDate,
            )
        ).thenReturn(true)

        assertThrows<PriceDateOverlapException> { createPriceUseCase.execute(command, productId) }

        verify(productPort).lockById(productId)
        verify(pricePort, never()).save(any<Price>(), any<UUID>())
    }

    private fun createPriceCommand() =
        CreatePriceCommand(
            value = BigDecimal("99.123456"),
            initDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 2, 1)
        )

}