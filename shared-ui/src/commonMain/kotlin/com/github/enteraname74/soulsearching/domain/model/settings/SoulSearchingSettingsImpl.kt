package com.github.enteraname74.soulsearching.domain.model.settings

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
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

    /**
     * Set the current played music index and position to the settings.
     */
    override fun saveCurrentMusicInformation(
        currentMusicIndex: Int,
        currentMusicPosition: Int
    ) {
        set(SoulSearchingSettingsKeys.Player.PLAYER_MUSIC_INDEX_KEY.key, currentMusicIndex)
        set(SoulSearchingSettingsKeys.Player.PLAYER_MUSIC_POSITION_KEY.key, currentMusicPosition)
    }
}