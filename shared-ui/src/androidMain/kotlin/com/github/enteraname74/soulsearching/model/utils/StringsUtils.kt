package com.github.enteraname74.soulsearching.model.utils

import androidx.appcompat.app.AppCompatDelegate
import com.github.enteraname74.soulsearching.coreui.strings.EnStrings
import com.github.enteraname74.soulsearching.coreui.strings.FrStrings
import com.github.enteraname74.soulsearching.coreui.strings.Strings

object StringsUtils {
    fun getStrings(): Strings {
        val localeList = AppCompatDelegate.getApplicationLocales()
        return when(localeList[0]?.language ?: "en") {
            "fr" -> FrStrings
            else -> EnStrings
        }
    }
}