package com.github.enteraname74.soulsearching.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
enum class ColorThemeType(val value: Int) {
    DYNAMIC(0),
    SYSTEM(1),
    PERSONALIZED(2);

    companion object {
        fun fromOrDefault(value: Int): ColorThemeType = entries.firstOrNull { it.value == value } ?: DYNAMIC
    }
}
