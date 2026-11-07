package com.products.application.product.controller

import com.products.application.product.command.model.CreateProductCommand
import com.products.application.product.command.model.CreateProductRequest
import com.products.application.product.command.usecase.CreateProductUseCase
import com.products.application.product.domain.Product
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@WebMvcTest(ProductController::class)
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var createProductUseCase: CreateProductUseCase

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