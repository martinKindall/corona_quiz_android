package com.coronaquiz.webServices

import com.coronaquiz.dataClasses.GeoIpCountry
import com.coronaquiz.interfaces.PromiseEnd
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class GeoIpCountryService {

    fun getLocation(listener: GetLocationListener) {
        val client = HttpService.get()
        val request = Request.Builder()
            .url(
                "http://api.ipstack.com/check?access_key=6cfc9ba5e3921cb5d80e9443492d81b6&fields=latitude,longitude,country_code"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            @Throws
            override fun onFailure(call: Call, e: IOException) {
                ("get GeoIpLocation: Unexpected error $e").let {
                    FirebaseCrashlytics.getInstance().log(it)
                }
                e.printStackTrace()
                listener.onError(e)
                listener.finally()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    ("get GeoIpLocation: Unexpected code " + response).let {
                        FirebaseCrashlytics.getInstance().log(it)
                    }
                    listener.onError(null)
                } else {
                    val jsonEncoded: String? = response.body?.string()
                    val geoIpCountry: GeoIpCountry = Gson().fromJson(jsonEncoded, GeoIpCountry::class.java)
                    listener.getLocation(geoIpCountry)
                }
                listener.finally()
            }
        })
    }
}

interface GetLocationListener: PromiseEnd {
    fun getLocation(geoIpCountry: GeoIpCountry)
}