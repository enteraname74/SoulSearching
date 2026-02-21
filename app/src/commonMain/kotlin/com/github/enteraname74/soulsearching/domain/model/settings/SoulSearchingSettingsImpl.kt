package com.github.enteraname74.soulsearching.domain.model.settings

import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

/**
 * Implementation of the SoulSearchingSettings using the multiplatform-settings library
 */
class SoulSearchingSettingsImpl(
    private val settings: Settings
) : SoulSearchingSettings {

    override fun <T> set(key: String, value: T) {
        when(value) {
            is String -> settings.putString(key, value)
            is Boolean -> settings.putBoolean(key, value)
            is Int -> settings.putInt(key, value)
            is Long -> settings.putLong(key, value)
            is Float -> settings.putFloat(key, value)
            is Double -> settings.putDouble(key, value)
            is PlayerMode -> settings.putString(key, value.value)
            else -> throw IllegalArgumentException("SoulSearchingSettingsImpl: Wrong type used for storing: $value")
        }
        updateFlowValue<T>(key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(settingElement: SoulSearchingSettingElement<T>): T =
        with(settingElement) {
            when(defaultValue) {
                is String -> settings[key, defaultValue as String] as T
                is Boolean -> settings[key, defaultValue as Boolean] as T
                is Int -> settings[key, defaultValue as Int] as T
                is Long -> settings[key, defaultValue as Long] as T
                is Float -> settings[key, defaultValue as Float] as T
                is Double -> settings[key, defaultValue as Double] as T
                is PlayerMode -> PlayerMode.from(settings[key, (defaultValue as PlayerMode).value]) as T
                else -> throw IllegalArgumentException("SoulSearchingSettingsImpl: Wrong type used for retrieving: $settingElement")
            }
        }

    override fun <T> delete(element: SoulSearchingSettingElement<T>) {
        settings.remove(key = element.key)
        updateFlowValue<T>(key = element.key)
    }
}