package com.tamdao.webapp.entity

data class NonprofitRequest(
    val legalName: String = "",
    val ein: String = "",
    val mission: String = "",
    val address: Address = Address()
)