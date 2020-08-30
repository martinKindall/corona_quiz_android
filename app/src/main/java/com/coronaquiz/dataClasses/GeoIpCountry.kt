package com.coronaquiz.dataClasses

data class GeoIpCountry(
    val country_code: String
) {
    constructor(): this("PY")
}
