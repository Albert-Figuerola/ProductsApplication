package com.products.application.product.exception

import com.products.application.common.exception.DomainException
import java.util.UUID

class ProductNotFoundException(
    productId: UUID
) : DomainException(
    code = "PRODUCT_NOT_FOUND",
    message = "Product with id $productId not found"
)