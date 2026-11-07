package com.products.application.product.command.usecase

import com.products.application.product.command.model.CreateProductCommand
import com.products.application.product.domain.Product
import com.products.application.product.port.ProductPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateProductUseCase(
    private val productPort: ProductPort
) {

    fun execute(command: CreateProductCommand): Product {
        val product = Product(
            id = UUID.randomUUID(),
            name = command.name,
            description = command.description
        )

        return productPort.save(product)
    }

}