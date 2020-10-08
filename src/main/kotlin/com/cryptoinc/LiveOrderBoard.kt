package com.cryptoinc

import com.cryptoinc.model.CoinType
import com.cryptoinc.model.Order
import com.cryptoinc.model.OrderType
import com.cryptoinc.model.SummaryInfo
import com.cryptoinc.util.ListExtensions.sumByBigDecimal

class LiveOrderBoard {
    private val orders = mutableListOf<Order>();

    fun placeOrder(order: Order) {
        orders.add(order);
    }

    fun cancelOrder(order: Order) {
        if(!orders.contains(order)) {
            throw NoSuchOrderException(order)
        }
        orders.remove(order);
    }

    fun summary(orderType: OrderType, coinType: CoinType): List<SummaryInfo> {
        return orders
            .filter { it.orderType == orderType && it.coinType == coinType}
            .groupBy { it.unitPrice }
            .map { SummaryInfo(it.key, it.value.sumByBigDecimal { it.quantity }) }
            .sortedWith(orderType.summarySortOrder)
    }
}