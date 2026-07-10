package com.github.enteraname74.soulsearching.coreui.strings

import android.content.Context
import kotlin.collections.get

object StringsUtils {
    fun getStrings(
        context: Context,
    ): Strings {
        val localeList = context.resources.configuration.getLocales()

        return when(localeList[0]?.language ?: "en") {
            "fr" -> FrStrings
            else -> EnStrings
        }
    }
}