package com.products.application.product.port

import com.products.application.product.domain.Product

interface ProductPort {

    fun save(product: Product): Product

}