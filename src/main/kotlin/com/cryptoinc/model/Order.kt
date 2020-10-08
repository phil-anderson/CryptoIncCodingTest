package com.cryptoinc.model

import java.math.BigDecimal

data class Order(
    val userId: String,
    val orderType: OrderType,
    val coinType: CoinType,
    val unitPrice: BigDecimal,
    val quantity: BigDecimal
)