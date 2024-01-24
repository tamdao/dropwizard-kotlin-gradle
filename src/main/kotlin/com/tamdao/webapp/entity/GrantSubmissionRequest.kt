package com.tamdao.webapp.entity

data class GrantSubmissionRequest(
    val grantName: String = "",
    val requestedAmount: Long = 0,
    val awardedAmount: Long = 0,
    val grantType: GrantType = GrantType.OTHER,
    val tags: String = "",
    val duration: Duration = Duration()
)
