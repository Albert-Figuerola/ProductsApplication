package com.products.application.price.command.mapper

import com.products.application.price.command.model.CreatePriceCommand
import com.products.application.price.command.model.CreatePriceRequest

internal fun CreatePriceRequest.toCommand() =
    CreatePriceCommand(
        value = value,
        initDate = initDate,
        endDate = endDate
    )