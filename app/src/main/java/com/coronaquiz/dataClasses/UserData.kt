package com.coronaquiz.dataClasses

data class UserData(
    val username: String,
    val email: String,
    val countryCode: String
) {
    constructor(): this(
        "",
        "",
        ""
    )
}