package com.coronaquiz.dataClasses

data class RankingEntry(
    val username: String,
    val score: Int,
    val timeElapsed: Int,
    val countryCode: String,
    val internalScore: Int
) {
    constructor(): this(
        "",
        0,
        0,
        "",
        0
    )
}