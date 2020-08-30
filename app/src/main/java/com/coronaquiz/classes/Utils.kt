package com.coronaquiz.classes

import android.content.res.Resources
import android.util.TypedValue

class Utils {

    fun dpToPixel(
        resources: Resources,
        dpsQty: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpsQty,
            resources.displayMetrics
        ).toInt()
    }

    fun countryCodeToEmojiFlag(country: String): String {
        val flagOffset = 0x1F1E6
        val asciiOffset = 0x41

        val firstChar = Character.codePointAt(country, 0) - asciiOffset + flagOffset
        val secondChar = Character.codePointAt(country, 1) - asciiOffset + flagOffset

        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }
}