package com.tamdao.webapp.entity

data class Nonprofit(
    var id: Int,
    val legalName: String,
    val ein: String,
    val mission: String,
    val address: Address
)