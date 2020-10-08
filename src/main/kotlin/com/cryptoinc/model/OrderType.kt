package com.cryptoinc.model

enum class OrderType(val summarySortOrder: Comparator<SummaryInfo>) {
    Buy(compareByDescending {  it.unitPrice }),
    Sell(compareBy {  it.unitPrice });
}