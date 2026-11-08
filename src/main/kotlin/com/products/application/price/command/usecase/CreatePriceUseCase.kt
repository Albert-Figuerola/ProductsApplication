package com.products.application.price.command.usecase

import com.products.application.price.command.model.CreatePriceCommand
import com.products.application.price.domain.Price
import com.products.application.price.exception.PriceDateOverlapException
import com.products.application.price.port.PricePort
import com.products.application.product.port.ProductPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
class CreatePriceUseCase(
    private val productPort: ProductPort,
    private val pricePort: PricePort
) {

    fun execute(command: CreatePriceCommand, productId: UUID): Price {
        productPort.lockById(productId)

        val existsOverlappingPrice = pricePort.existsOverlappingPrice(
            productId,
            command.initDate,
            command.endDate
        )

        if (existsOverlappingPrice) throw PriceDateOverlapException(productId)

        val price = Price(
            id = UUID.randomUUID(),
            value = command.value,
            initDate = command.initDate,
            endDate = command.endDate
        )

        return pricePort.save(price, productId)
    }

}