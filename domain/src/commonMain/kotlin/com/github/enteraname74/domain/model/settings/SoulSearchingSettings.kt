package com.github.enteraname74.domain.model.settings

import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import kotlinx.coroutines.flow.Flow

/**
 * Represent the settings of a SoulSearching application where we can save key-value elements.
 */
interface SoulSearchingSettings {
    fun <T>set(key: String, value: T)
    fun <T>get(settingElement: SoulSearchingSettingElement<T>): T

    /**
     * Set the current played music index and position to the settings.
     */
    fun saveCurrentMusicInformation(
        currentMusicIndex: Int,
        currentMusicPosition: Int
    )

    fun <DataType>updateFlowValue(key: String) {
        SettingsFlowSystem.update<DataType>(key = key)
    }

    fun <T>getFlowOn(settingElement: SoulSearchingSettingElement<T>): Flow<T> {
        val settingFlowInformation = SettingFlowInformation(
            key = settingElement.key,
            retrieveValue = { get(settingElement) }
        )
        val returnedFlow: SettingFlowInformation<T> = SettingsFlowSystem.addFlowIfNotExist(
            settingFlowInformation = settingFlowInformation,
        )
        return returnedFlow.flow
    }
}

interface SoulSearchingSettingElement<T> {
    val key: String
    val defaultValue: T
}

fun <T>settingElementOf(key: String, defaultValue: T): SoulSearchingSettingElement<T> =
    object : SoulSearchingSettingElement<T> {
        override val key: String = key
        override val defaultValue: T = defaultValue
    }

object SoulSearchingSettingsKeys {
    const val SHARED_PREF_KEY = "SOUL_SEARCHING_SHARED_PREF"

    val HAS_MUSICS_BEEN_FETCHED_KEY = settingElementOf(
        key = "MUSICS_FETCHED",
        defaultValue = false,
    )

    val IS_MUSIC_FILE_MODIFICATION_ON = settingElementOf(
        key = "IS_MUSIC_FILE_MODIFICATION_ON",
        defaultValue = true
    )

    object Sort {
        val SORT_MUSICS_TYPE_KEY = settingElementOf(
            key = "SORT_MUSICS_TYPE",
            defaultValue = SortType.NAME,
        )
        val SORT_MUSICS_DIRECTION_KEY = settingElementOf(
            key = "SORT_MUSICS_DIRECTION",
            defaultValue = SortDirection.ASC,
        )

        val SORT_ALBUMS_TYPE_KEY = settingElementOf(
            key = "SORT_ALBUMS_TYPE",
            defaultValue = SortType.NAME,
        )
        val SORT_ALBUMS_DIRECTION_KEY = settingElementOf(
            key = "SORT_ALBUMS_DIRECTION",
            defaultValue = SortDirection.ASC,
        )

        val SORT_ARTISTS_TYPE_KEY = settingElementOf(
            key = "SORT_ARTISTS_TYPE",
            defaultValue = SortType.NAME,
        )
        val SORT_ARTISTS_DIRECTION_KEY = settingElementOf(
            key = "SORT_ARTISTS_DIRECTION",
            defaultValue = SortDirection.ASC,
        )

        val SORT_PLAYLISTS_TYPE_KEY = settingElementOf(
            key = "SORT_PLAYLISTS_TYPE",
            defaultValue = SortType.NAME,
        )
        val SORT_PLAYLISTS_DIRECTION_KEY = settingElementOf(
            key = "SORT_PLAYLISTS_DIRECTION",
            defaultValue = SortDirection.ASC
        )
    }

    object Player {
        val PLAYER_MUSIC_INDEX_KEY = settingElementOf(
            key = "PLAYER_MUSIC_INDEX",
            defaultValue = -1,
        )
        val PLAYER_MUSIC_POSITION_KEY = settingElementOf(
            key = "PLAYER_MUSIC_POSITION",
            defaultValue = 0,
        )
        val PLAYER_MODE_KEY = settingElementOf(
            key = "PLAYER_MODE_KEY",
            defaultValue = PlayerMode.Normal,
        )
        val IS_PLAYER_SWIPE_ENABLED = settingElementOf(
            key = "IS_PLAYER_SWIPE_ENABLED",
            defaultValue = true,
        )
        val SOUL_MIX_TOTAL_BY_LIST = settingElementOf(
            key = "SOUL_MIX_TOTAL_BY_LIST",
            defaultValue = 50
        )
    }

    object ColorTheme {
        val COLOR_THEME_KEY = settingElementOf(
            key = "COLOR_THEME",
            defaultValue = 0,
        )
        val DYNAMIC_PLAYER_THEME = settingElementOf(
            key = "DYNAMIC_PLAYER_THEME",
            defaultValue = false,
        )
        val DYNAMIC_PLAYLIST_THEME = settingElementOf(
            key = "DYNAMIC_PLAYLIST_THEME",
            defaultValue = false,
        )
        val DYNAMIC_OTHER_VIEWS_THEME = settingElementOf(
            key = "DYNAMIC_OTHER_VIEWS_THEME",
            defaultValue = false
        )
        val FORCE_DARK_THEME_KEY = settingElementOf(
            key = "FORCE_DARK_THEME_KEY",
            defaultValue = false,
        )
        val FORCE_LIGHT_THEME_KEY = settingElementOf(
            key = "FORCE_LIGHT_THEME_KEY",
            defaultValue = false,
        )
    }

    object MainPage {
        val IS_QUICK_ACCESS_SHOWN = settingElementOf(
            key = "IS_QUICK_ACCESS_SHOWN",
            defaultValue = true,
        )
        val IS_PLAYLISTS_SHOWN = settingElementOf(
            key = "IS_PLAYLISTS_SHOWN",
            defaultValue = true,
        )
        val IS_ALBUMS_SHOWN = settingElementOf(
            key = "IS_ALBUMS_SHOWN",
            defaultValue = true,
        )
        val IS_ARTISTS_SHOWN = settingElementOf(
            key = "IS_ARTISTS_SHOWN",
            defaultValue = true,
        )
        val ARE_MUSICS_BY_MONTHS_SHOWN = settingElementOf(
            key = "ARE_MUSICS_BY_MONTHS_SHOWN",
            defaultValue = false
        )
        val ARE_MUSICS_BY_FOLDERS_SHOWN = settingElementOf(
            key = "ARE_MUSICS_BY_FOLDERS_SHOWN",
            defaultValue = true,
        )
    }
}