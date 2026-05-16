package com.estrano.starter.model

data class PortfolioItem(
    val category: String,
    val title: String,
    val description: String,
    val stack: String
)

data class ServiceItem(
    val iconRes: Int,
    val title: String,
    val description: String,
    val badge: String
)
