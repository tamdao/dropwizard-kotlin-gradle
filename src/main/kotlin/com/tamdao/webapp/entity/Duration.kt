package com.tamdao.webapp.entity

import java.time.LocalDate

data class Duration(
    val grantStart: LocalDate? = null,
    val grantEnd: LocalDate? = null
)
