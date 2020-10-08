package com.cryptoinc.model

import java.math.BigDecimal

data class SummaryInfo(
    val unitPrice: BigDecimal,
    val quantity: BigDecimal
)