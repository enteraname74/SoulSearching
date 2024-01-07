package com.github.soulsearching.classes.utils

import android.content.SharedPreferences
import com.github.soulsearching.classes.types.ColorThemeType
import com.github.soulsearching.classes.types.PlayerMode
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utils for managing the shared preferences.
 */
object SharedPrefUtils {
    lateinit var sharedPreferences : SharedPreferences

    /**
     * Update an integer value to the shared preferences.
     */
    fun updateIntValue(keyToUpdate: String, newValue: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            with(sharedPreferences.edit()) {
                putInt(keyToUpdate, newValue)
                apply()
            }
        }
    }

    /**
     * Update a boolean value to the shared preferences.
     */
    fun updateBooleanValue(keyToUpdate: String, newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            with(sharedPreferences.edit()) {
                putBoolean(keyToUpdate, newValue)
                apply()
            }
        }
    }

    /**
     * Define to the shared preferences that the musics has been fetched.
     */
    fun setMusicsFetched(){
        CoroutineScope(Dispatchers.IO).launch {
            with(sharedPreferences.edit()) {
                putBoolean(HAS_MUSICS_BEEN_FETCHED_KEY, true)
                apply()
            }
        }
    }

    /**
     * Check if the musics has been fetched.
     */
    fun hasMusicsBeenFetched() : Boolean {
        return sharedPreferences.getBoolean(
            HAS_MUSICS_BEEN_FETCHED_KEY,
            false
        )
    }

    /**
     * Tries to retrieve and set the last played music from the previous app session.
     */
    fun getPlayerSavedCurrentMusic() {
        val index = sharedPreferences.getInt(PLAYER_MUSIC_INDEX_KEY, -1)
        val position = sharedPreferences.getInt(PLAYER_MUSIC_POSITION_KEY, 0)

        PlayerUtils.playerViewModel.setMusicFromIndex(index)
        PlayerUtils.playerViewModel.currentMusicPosition = position
    }

    /**
     * Set the current played music index to the shared preferences.
     */
    fun setPlayerSavedCurrentMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            with(sharedPreferences.edit()) {
                putInt(PLAYER_MUSIC_INDEX_KEY, PlayerUtils.playerViewModel.getIndexOfCurrentMusic())
                apply()
            }
        }
        setCurrentMusicPosition()
    }

    /**
     * Retrieve the player mode from the last app session.
     */
    fun getPlayerMode() {
        val playerMode = PlayerMode.valueOf(sharedPreferences.getString(PLAYER_MODE_KEY, "NORMAL")!!)
        PlayerUtils.playerViewModel.playerMode = playerMode
    }

    /**
     * Define the player mode to save for the next app session.
     */
    fun setPlayerMode() {
        CoroutineScope(Dispatchers.IO).launch {
            with(sharedPreferences.edit()) {
                putString(PLAYER_MODE_KEY, PlayerUtils.playerViewModel.playerMode.toString())
                apply()
            }
        }
    }

    /**
     * Define the current music position.
     * It is useful when launching a new app session for starting a song from where it stopped previously.
     */
    fun setCurrentMusicPosition() {
        CoroutineScope(Dispatchers.IO).launch {
            with(sharedPreferences.edit()) {
                putInt(PLAYER_MUSIC_POSITION_KEY, PlayerUtils.playerViewModel.currentMusicPosition)
                apply()

            }
        }
    }

    /**
     * Initialize the settings.
     */
    fun initializeSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            SettingsUtils.settingsViewModel.apply {
                colorTheme = sharedPreferences.getInt(
                    COLOR_THEME_KEY, ColorThemeType.DYNAMIC
                )
                isDynamicPlayerThemeSelected = sharedPreferences.getBoolean(
                    DYNAMIC_PLAYER_THEME, false
                )
                isDynamicPlaylistThemeSelected = sharedPreferences.getBoolean(
                    DYNAMIC_PLAYLIST_THEME, false
                )
                isQuickAccessShown = sharedPreferences.getBoolean(
                    IS_QUICK_ACCESS_SHOWN, true
                )
                isPlaylistsShown = sharedPreferences.getBoolean(
                    IS_PLAYLISTS_SHOWN, true
                )
                isAlbumsShown = sharedPreferences.getBoolean(
                    IS_ALBUMS_SHOWN, true
                )
                isArtistsShown = sharedPreferences.getBoolean(
                    IS_ARTISTS_SHOWN, true
                )
                isVerticalBarShown = sharedPreferences.getBoolean(
                    IS_VERTICAL_BAR_SHOWN, false
                )
            }
        }
    }

    /**
     * Initialize the sorts used by the application.
     */
    fun initializeSorts(
        onMusicEvent: (MusicEvent) -> Unit,
        onPlaylistEvent: (PlaylistEvent) -> Unit,
        onAlbumEvent: (AlbumEvent) -> Unit,
        onArtistEvent: (ArtistEvent) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            onMusicEvent(
                MusicEvent.SetSortDirection(
                    sharedPreferences.getInt(
                        SORT_MUSICS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onMusicEvent(
                MusicEvent.SetSortType(
                    sharedPreferences.getInt(
                        SORT_MUSICS_TYPE_KEY, SortType.NAME
                    )
                )
            )
            onPlaylistEvent(
                PlaylistEvent.SetSortDirection(
                    sharedPreferences.getInt(
                        SORT_PLAYLISTS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onPlaylistEvent(
                PlaylistEvent.SetSortType(
                    sharedPreferences.getInt(
                        SORT_PLAYLISTS_TYPE_KEY, SortType.NAME
                    )
                )
            )
            onAlbumEvent(
                AlbumEvent.SetSortDirection(
                    sharedPreferences.getInt(
                        SORT_ALBUMS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onAlbumEvent(
                AlbumEvent.SetSortType(
                    sharedPreferences.getInt(
                        SORT_ALBUMS_TYPE_KEY, SortType.NAME
                    )
                )
            )
            onArtistEvent(
                ArtistEvent.SetSortDirection(
                    sharedPreferences.getInt(
                        SORT_ARTISTS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onArtistEvent(
                ArtistEvent.SetSortType(
                    sharedPreferences.getInt(
                        SORT_ARTISTS_TYPE_KEY, SortType.NAME
                    )
                )
            )
        }
    }

    const val SHARED_PREF_KEY = "SOUL_SEARCHING_SHARED_PREF"

    private const val HAS_MUSICS_BEEN_FETCHED_KEY = "MUSICS_FETCHED"

    const val SORT_MUSICS_TYPE_KEY = "SORT_MUSICS_TYPE"
    const val SORT_MUSICS_DIRECTION_KEY = "SORT_MUSICS_DIRECTION"

    const val SORT_ALBUMS_TYPE_KEY = "SORT_ALBUMS_TYPE"
    const val SORT_ALBUMS_DIRECTION_KEY = "SORT_ALBUMS_DIRECTION"

    const val SORT_ARTISTS_TYPE_KEY = "SORT_ARTISTS_TYPE"
    const val SORT_ARTISTS_DIRECTION_KEY = "SORT_ARTISTS_DIRECTION"

    const val SORT_PLAYLISTS_TYPE_KEY = "SORT_PLAYLISTS_TYPE"
    const val SORT_PLAYLISTS_DIRECTION_KEY = "SORT_PLAYLISTS_DIRECTION"

    private const val PLAYER_MUSIC_INDEX_KEY = "PLAYER_MUSIC_INDEX"
    private const val PLAYER_MUSIC_POSITION_KEY = "PLAYER_MUSIC_POSITION"
    private const val PLAYER_MODE_KEY = "PLAYER_MODE_KEY"

    const val COLOR_THEME_KEY = "COLOR_THEME"
    const val DYNAMIC_PLAYER_THEME = "DYNAMIC_PLAYER_THEME"
    const val DYNAMIC_PLAYLIST_THEME = "DYNAMIC_PLAYLIST_THEME"
    const val DYNAMIC_OTHER_VIEWS_THEME = "DYNAMIC_OTHER_VIEWS_THEME"

    const val IS_QUICK_ACCESS_SHOWN = "IS_QUICK_ACCESS_SHOWN"
    const val IS_PLAYLISTS_SHOWN = "IS_PLAYLISTS_SHOWN"
    const val IS_ALBUMS_SHOWN = "IS_ALBUMS_SHOWN"
    const val IS_ARTISTS_SHOWN = "IS_ARTISTS_SHOWN"

    const val IS_VERTICAL_BAR_SHOWN = "IS_VERTICAL_BAR_SHOWN"
}