package com.cryptoinc

import com.cryptoinc.model.CoinType.Etherium
import com.cryptoinc.model.CoinType.Litecoin
import com.cryptoinc.model.Order
import com.cryptoinc.model.OrderType.Buy
import com.cryptoinc.model.OrderType.Sell
import com.cryptoinc.model.SummaryInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class LiveOrderBoardTest {
    private val liveOrderBoard = LiveOrderBoard()

    @Test
    fun `Orders can be placed`() {
        assertThat(liveOrderBoard.summary(Buy, Etherium)).isEmpty()

        val order = Order("TestUser", Buy, Etherium, BigDecimal(1), BigDecimal(10))
        liveOrderBoard.placeOrder(order)
        val summary = liveOrderBoard.summary(Buy, Etherium)

        assertThat(summary).containsExactly(
            SummaryInfo(order.unitPrice, order.quantity)
        )
    }

    @Test
    fun `A specific order can be cancelled`() {
        val firstOrder = Order("TestUser", Buy, Etherium, BigDecimal(1), BigDecimal(10))
        val secondOrder = Order("TestUser", Buy, Etherium, BigDecimal(2), BigDecimal(10))

        with(liveOrderBoard) {
            placeOrder(firstOrder)
            placeOrder(secondOrder)
        }
        assertThat(liveOrderBoard.summary(Buy, Etherium)).hasSize(2)

        liveOrderBoard.cancelOrder(secondOrder)
        assertThat(liveOrderBoard.summary(Buy, Etherium)).containsExactly(
            SummaryInfo(firstOrder.unitPrice, firstOrder.quantity)
        )
    }

    @Test
    fun `Throws if the order being cancelled doesn't exist`() {
        assertThrows<NoSuchOrderException> {
            liveOrderBoard.cancelOrder(Order("TestUser", Buy, Etherium, BigDecimal(10), BigDecimal(10)))
        }
    }

    @Test
    fun `Summary sums orders with the same price regardless of user`() {
        with(liveOrderBoard) {
            placeOrder(Order("TestUser", Buy, Etherium, BigDecimal(10), BigDecimal(1)))
            placeOrder(Order("AnotherUser", Buy, Etherium, BigDecimal(10), BigDecimal(3)))
            placeOrder(Order("YetAnotherUser", Buy, Etherium, BigDecimal(20), BigDecimal(5)))
            placeOrder(Order("OneMoreUser", Buy, Etherium, BigDecimal(20), BigDecimal(7)))
        }

        val summary = liveOrderBoard.summary(Buy, Etherium)
        assertThat(summary).containsExactly(
            SummaryInfo(BigDecimal(20), BigDecimal(12)),
            SummaryInfo(BigDecimal(10), BigDecimal(4))
        )
    }

    @Test
    fun `Summary for BUY orders is sorted by unit price, highest first`() {
        with(liveOrderBoard) {
            placeOrder(Order("TestUser", Buy, Etherium, BigDecimal(10), BigDecimal(1)))
            placeOrder(Order("AnotherUser", Buy, Etherium, BigDecimal(20), BigDecimal(3)))
            placeOrder(Order("YetAnotherUser", Buy, Etherium, BigDecimal(20), BigDecimal(5)))
            placeOrder(Order("OneMoreUser", Buy, Etherium, BigDecimal(30), BigDecimal(7)))
        }

        val summary = liveOrderBoard.summary(Buy, Etherium)
        assertThat(summary).containsExactly(
            SummaryInfo(BigDecimal(30), BigDecimal(7)),
            SummaryInfo(BigDecimal(20), BigDecimal(8)),
            SummaryInfo(BigDecimal(10), BigDecimal(1))
        )
    }

    @Test
    fun `Summary for SELL orders is sorted by unit price, lowest first`() {
        with(liveOrderBoard) {
            placeOrder(Order("TestUser", Sell, Etherium, BigDecimal(10), BigDecimal(1)))
            placeOrder(Order("AnotherUser", Sell, Etherium, BigDecimal(20), BigDecimal(3)))
            placeOrder(Order("YetAnotherUser", Sell, Etherium, BigDecimal(20), BigDecimal(5)))
            placeOrder(Order("OneMoreUser", Sell, Etherium, BigDecimal(30), BigDecimal(7)))
        }

        val summary = liveOrderBoard.summary(Sell, Etherium)
        assertThat(summary).containsExactly(
            SummaryInfo(BigDecimal(10), BigDecimal(1)),
            SummaryInfo(BigDecimal(20), BigDecimal(8)),
            SummaryInfo(BigDecimal(30), BigDecimal(7))
        )
    }

    @Test
    fun `Summary is filtered by order type and coin type`() {
        with(liveOrderBoard) {
            placeOrder(Order("TestUser", Buy, Etherium, BigDecimal(10), BigDecimal(1)))
            placeOrder(Order("AnotherUser", Sell, Litecoin, BigDecimal(10), BigDecimal(3)))
            placeOrder(Order("YetAnotherUser", Buy, Litecoin, BigDecimal(10), BigDecimal(5)))
            placeOrder(Order("OneMoreUser", Sell, Etherium, BigDecimal(10), BigDecimal(7)))
        }

        assertThat(liveOrderBoard.summary(Buy, Etherium)).containsExactly(
            SummaryInfo(BigDecimal(10), BigDecimal(1))
        )

        assertThat(liveOrderBoard.summary(Sell, Litecoin)).containsExactly(
            SummaryInfo(BigDecimal(10), BigDecimal(3))
        )

        assertThat(liveOrderBoard.summary(Buy, Litecoin)).containsExactly(
            SummaryInfo(BigDecimal(10), BigDecimal(5))
        )
        assertThat(liveOrderBoard.summary(Sell, Etherium)).containsExactly(
            SummaryInfo(BigDecimal(10), BigDecimal(7))
        )
    }

    @Test
    fun `Handles the sample scenario provided on the test paper`() {
        with(liveOrderBoard) {
            placeOrder(Order("user1", Sell, Etherium, BigDecimal(13.6), BigDecimal(350.1)))
            placeOrder(Order("user2", Sell, Etherium, BigDecimal(14), BigDecimal(50.5)))
            placeOrder(Order("user3", Sell, Etherium, BigDecimal(   13.9), BigDecimal(441.8)))
            placeOrder(Order("user4", Sell, Etherium, BigDecimal(13.6), BigDecimal(3.5)))
        }

        val summary = liveOrderBoard.summary(Sell, Etherium)
        assertThat(summary).containsExactly(
            SummaryInfo(BigDecimal(13.6), BigDecimal(353.6)),
            SummaryInfo(BigDecimal(13.9), BigDecimal(441.8)),
            SummaryInfo(BigDecimal(14), BigDecimal(50.5))
        )
    }
}