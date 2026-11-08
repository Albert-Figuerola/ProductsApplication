package com.products.application.product.usecase

import com.products.application.product.command.model.CreateProductCommand
import com.products.application.product.command.usecase.CreateProductUseCase
import com.products.application.product.domain.Product
import com.products.application.product.port.ProductPort
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class CreateProductUseCaseTest {

    private val productPort: ProductPort = mock()
    private val createProductUseCase = CreateProductUseCase(productPort)

    @Test
    fun `should create a product successfully`() {
        val command = createProductCommand()

        whenever(productPort.save(any<Product>()))
            .thenAnswer { it.getArgument<Product>(0) }

        val result = createProductUseCase.execute(command)

        assertEquals(command.name, result.name)
        assertEquals(command.description, result.description)

        verify(productPort).save(result)
    }

    private fun createProductCommand() =
        CreateProductCommand(
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada",
        )

}