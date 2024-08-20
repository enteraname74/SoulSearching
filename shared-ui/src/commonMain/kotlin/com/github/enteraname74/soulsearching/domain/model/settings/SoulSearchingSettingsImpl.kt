package com.github.enteraname74.soulsearching.domain.model.settings

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.PlayerMode
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

/**
 * Implementation of the SoulSearchingSettings using the multiplatform-settings library
 */
class SoulSearchingSettingsImpl(
    private val settings: Settings
) : SoulSearchingSettings {

    /**
     * Save an Integer related to a given key.
     */
    override fun setInt(key: String, value: Int) {
        settings.putInt(key, value)
        updateFlowValue<Int>(key)
    }

    /**
     * Save a String related to a given key.
     */
    override fun setString(key: String, value: String) {
        settings.putString(key, value)
        updateFlowValue<String>(key)
    }

    /**
     * Save a Boolean related to a given key.
     */
    override fun setBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
        updateFlowValue<Boolean>(key)
    }

    /**
     * Tries to retrieve an Integer related to a given key.
     */
    override fun getInt(key: String, defaultValue: Int): Int = settings[key, defaultValue]

    /**
     * Tries to retrieve a Boolean related to a given key.
     */
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        settings[key, defaultValue]

    /**
     * Tries to retrieve a String related to a given key.
     */
    override fun getString(key: String, defaultValue: String): String = settings[key, defaultValue]

    /**
     * Set the current played music index and position to the settings.
     */
    override fun saveCurrentMusicInformation(
        currentMusicIndex: Int,
        currentMusicPosition: Int
    ) {
        setInt(SoulSearchingSettings.PLAYER_MUSIC_INDEX_KEY, currentMusicIndex)
        setInt(SoulSearchingSettings.PLAYER_MUSIC_POSITION_KEY, currentMusicPosition)
    }

    override fun setPlayerMode(key: String, value: PlayerMode) {
        setString(key, value.toString())
    }

    /**
     * Retrieve the player mode from the last app session.
     */
    override fun getPlayerMode(): PlayerMode {
        return PlayerMode.valueOf(getString(SoulSearchingSettings.PLAYER_MODE_KEY, "NORMAL"))
    }
}