package com.anzer.accountbook.data.model

data class MonthlySummary(
    val income: Double,
    val expense: Double,
    val balance: Double = income - expense
)
