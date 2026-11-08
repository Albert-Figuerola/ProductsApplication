package com.products.application.price.exception

import com.products.application.shared.exception.DomainException
import java.util.UUID

class PriceDateOverlapException(
    productId: UUID
) : DomainException(
    code = "PRICE_DATE_OVERLAP",
    message = "Price date range overlaps with an existing price for product with id: $productId"
)