package com.cryptoinc

import com.cryptoinc.model.Order

class NoSuchOrderException(order: Order): RuntimeException("Order not found: $order")