package com.cryptoinc.util

import java.math.BigDecimal
import java.math.BigDecimal.ZERO

object ListExtensions {
    fun <T> List<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
        return fold(ZERO) { sum, it -> sum + selector(it) }
    }
}