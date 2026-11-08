package com.products.application.price.controller

import com.products.application.price.command.model.CreatePriceCommand
import com.products.application.price.command.model.CreatePriceRequest
import com.products.application.price.command.usecase.CreatePriceUseCase
import com.products.application.price.domain.Price
import com.products.application.shared.extension.roundToTwoDecimals
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
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
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@WebMvcTest(PriceController::class)
class PriceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var createPriceUseCase: CreatePriceUseCase

    @Test
    fun `should return 201 when a price is created`() {
        val request = createPriceRequest()
        val price = createPrice(request)
        val productId = UUID.randomUUID()

        whenever(
            createPriceUseCase.execute(
                any<CreatePriceCommand>(),
                eq(productId)
            )
        ).thenReturn(price)

        mockMvc.perform(
            post("/api/v1/products/${productId}/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

            .andExpect(jsonPath("$.data.id").value(price.id.toString()))
            .andExpect(jsonPath("$.data.value").value(99.12))
            .andExpect(jsonPath("$.data.initDate").value(price.initDate.toString()))
            .andExpect(jsonPath("$.data.endDate").value(price.endDate.toString()))

        verify(createPriceUseCase).execute(
            CreatePriceCommand(
                value = request.value,
                initDate = request.initDate,
                endDate = request.endDate
            ),
            productId
        )
    }

    @Test
    fun `should return 201 when a price is created without end date`() {
        val request = createPriceRequest(null)
        val price = createPrice(request)
        val productId = UUID.randomUUID()

        whenever(
            createPriceUseCase.execute(
                any<CreatePriceCommand>(),
                eq(productId)
            )
        ).thenReturn(price)

        mockMvc.perform(
            post("/api/v1/products/${productId}/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

            .andExpect(jsonPath("$.data.id").value(price.id.toString()))
            .andExpect(jsonPath("$.data.value").value(99.12))
            .andExpect(jsonPath("$.data.initDate").value(price.initDate.toString()))
            .andExpect(jsonPath("$.data.endDate").value(nullValue()))

        verify(createPriceUseCase).execute(
            CreatePriceCommand(
                value = request.value,
                initDate = request.initDate,
                endDate = request.endDate
            ),
            productId
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["0", "-1", "-99.99"])
    fun `should return 400 when price value is not positive`(value: String) {
        val request = createPriceRequest().copy(value = BigDecimal(value))
        val productId = UUID.randomUUID()

        mockMvc.perform(
            post("/api/v1/products/${productId}/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createPriceUseCase)
    }

    @Test
    fun `should return 400 when price value is missing`() {
        val request = """
            {
                "initDate": "2024-01-01",
                "endDate": "2024-02-01"
            }
        """.trimIndent()
        val productId = UUID.randomUUID()

        mockMvc.perform(
            post("/api/v1/products/${productId}/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createPriceUseCase)
    }

    @Test
    fun `should return 400 when init date is missing`() {
        val request = """
            {       
                "value": "99.123456",
                "endDate": "2024-02-01"
            }
        """.trimIndent()
        val productId = UUID.randomUUID()

        mockMvc.perform(
            post("/api/v1/products/${productId}/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createPriceUseCase)
    }

    @Test
    fun `should return 400 when product id is invalid`() {
        val request = createPriceRequest()
        val productId = "12345"

        mockMvc.perform(
            post("/api/v1/products/${productId}/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createPriceUseCase)
    }

    private fun createPriceRequest(
        endDate: LocalDate? = LocalDate.of(2024, 2, 1),
    ) =
        CreatePriceRequest(
            value = BigDecimal("99.123456"),
            initDate = LocalDate.of(2024, 1, 1),
            endDate = endDate
        )

    private fun createPrice(request: CreatePriceRequest) =
        Price(
            id = UUID.randomUUID(),
            value = request.value,
            initDate = request.initDate,
            endDate = request.endDate
        )

}