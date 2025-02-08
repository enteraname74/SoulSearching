package com.github.enteraname74.soulsearching.model.utils

import android.content.Context
import com.github.enteraname74.soulsearching.coreui.strings.EnStrings
import com.github.enteraname74.soulsearching.coreui.strings.FrStrings
import com.github.enteraname74.soulsearching.coreui.strings.Strings

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