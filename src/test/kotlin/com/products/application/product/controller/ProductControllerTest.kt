package com.products.application.product.controller

import com.products.application.price.domain.Price
import com.products.application.product.command.model.CreateProductCommand
import com.products.application.product.command.model.CreateProductRequest
import com.products.application.product.command.usecase.CreateProductUseCase
import com.products.application.product.domain.Product
import com.products.application.product.exception.ProductNotFoundException
import com.products.application.product.query.model.ProductWithPrices
import com.products.application.product.query.usecase.GetProductPriceHistoryUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@WebMvcTest(ProductController::class)
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var createProductUseCase: CreateProductUseCase

    @MockitoBean
    private lateinit var getProductPriceHistoryUseCase: GetProductPriceHistoryUseCase

    @Test
    fun `should return 201 when a product is created`() {
        val request = createProductRequest()
        val product = createProduct(request)

        whenever(createProductUseCase.execute(any<CreateProductCommand>()))
            .thenReturn(product)

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

            .andExpect(jsonPath("$.data.id").value(product.id.toString()))
            .andExpect(jsonPath("$.data.name").value(product.name))
            .andExpect(jsonPath("$.data.description").value(product.description))

        verify(createProductUseCase).execute(
            CreateProductCommand(
                name = request.name,
                description = request.description
            )
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `should return 400 when name is blank`(name: String) {
        val request = createProductRequest().copy(name = name)

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createProductUseCase)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `should return 400 when description is blank`(description: String) {
        val request = createProductRequest().copy(description = description)

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createProductUseCase)
    }

    @Test
    fun `should return 200 with product price history`() {
        val productId = UUID.randomUUID()

        val productWithPrices = ProductWithPrices(
            id = productId,
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada",
            prices = listOf(
                Price(
                    id = UUID.randomUUID(),
                    value = BigDecimal("99.123456"),
                    initDate = LocalDate.of(2024, 1, 1),
                    endDate = LocalDate.of(2024, 6, 30)
                ),
                Price(
                    id = UUID.randomUUID(),
                    value = BigDecimal("199.987654"),
                    initDate = LocalDate.of(2025, 1, 1),
                    endDate = LocalDate.of(2025, 6, 30)
                )
            )
        )

        whenever(getProductPriceHistoryUseCase.execute(productId))
            .thenReturn(productWithPrices)

        mockMvc.perform(
            get("/api/v1/products/$productId/prices")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Zapatillas deportivas"))
            .andExpect(jsonPath("$.description").value("Modelo 2025 edición limitada"))
            .andExpect(jsonPath("$.prices.length()").value(2))
            .andExpect(jsonPath("$.prices[0].value").value(99.12))
            .andExpect(jsonPath("$.prices[0].initDate").value("2024-01-01"))
            .andExpect(jsonPath("$.prices[0].endDate").value("2024-06-30"))
            .andExpect(jsonPath("$.prices[1].value").value(199.99))
            .andExpect(jsonPath("$.prices[1].initDate").value("2025-01-01"))
            .andExpect(jsonPath("$.prices[1].endDate").value("2025-06-30"))

        Mockito.verify(getProductPriceHistoryUseCase).execute(productId)
    }

    @Test
    fun `should return 200 with empty price history`() {
        val productId = UUID.randomUUID()

        val productWithPrices = ProductWithPrices(
            id = productId,
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada",
            prices = emptyList()
        )

        whenever(getProductPriceHistoryUseCase.execute(productId))
            .thenReturn(productWithPrices)

        mockMvc.perform(
            get("/api/v1/products/$productId/prices")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Zapatillas deportivas"))
            .andExpect(jsonPath("$.prices").isArray)
            .andExpect(jsonPath("$.prices.length()").value(0))

        Mockito.verify(getProductPriceHistoryUseCase).execute(productId)
    }

    @Test
    fun `should return 404 when product price history is requested for nonexistent product`() {
        val productId = UUID.randomUUID()

        whenever(getProductPriceHistoryUseCase.execute(productId))
            .thenThrow(ProductNotFoundException(productId))

        mockMvc.perform(
            get("/api/v1/products/$productId/prices")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
            .andExpect(
                jsonPath("$.message").value("Product with id $productId not found")
            )

        Mockito.verify(getProductPriceHistoryUseCase).execute(productId)
    }

    private fun createProductRequest() =
        CreateProductRequest(
            name = "Zapatillas deportivas",
            description = "Modelo 2025 edición limitada"
        )

    private fun createProduct(request: CreateProductRequest) =
        Product(
            id = UUID.randomUUID(),
            name = request.name,
            description = request.description
        )

}