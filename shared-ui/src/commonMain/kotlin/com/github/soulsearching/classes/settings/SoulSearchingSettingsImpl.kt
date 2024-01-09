package com.github.soulsearching.classes.settings

import com.github.soulsearching.classes.types.PlayerMode
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Implementation of the SoulSearchingSettings using the multiplatform-settings library
 */
class SoulSearchingSettingsImpl(
    private val settings: Settings
) : SoulSearchingSettings {

    /**
     * Save an Integer related to a given key.
     */
    override fun setInt(key: String, value: Int) = settings.putInt(key, value)

    /**
     * Save a String related to a given key.
     */
    override fun setString(key: String, value: String) = settings.putString(key, value)

    /**
     * Save a Boolean related to a given key.
     */
    override fun setBoolean(key: String, value: Boolean) = settings.putBoolean(key, value)

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

    /**
     * Initialize the sorts used by the application.
     */
    override fun initializeSorts(
        onMusicEvent: (MusicEvent) -> Unit,
        onPlaylistEvent: (PlaylistEvent) -> Unit,
        onAlbumEvent: (AlbumEvent) -> Unit,
        onArtistEvent: (ArtistEvent) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            onMusicEvent(
                MusicEvent.SetSortDirection(
                    getInt(
                        SoulSearchingSettings.SORT_MUSICS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onMusicEvent(
                MusicEvent.SetSortType(
                    getInt(
                        SoulSearchingSettings.SORT_MUSICS_TYPE_KEY, SortType.NAME
                    )
                )
            )
            onPlaylistEvent(
                PlaylistEvent.SetSortDirection(
                    getInt(
                        SoulSearchingSettings.SORT_PLAYLISTS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onPlaylistEvent(
                PlaylistEvent.SetSortType(
                    getInt(
                        SoulSearchingSettings.SORT_PLAYLISTS_TYPE_KEY, SortType.NAME
                    )
                )
            )
            onAlbumEvent(
                AlbumEvent.SetSortDirection(
                    getInt(
                        SoulSearchingSettings.SORT_ALBUMS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onAlbumEvent(
                AlbumEvent.SetSortType(
                    getInt(
                        SoulSearchingSettings.SORT_ALBUMS_TYPE_KEY, SortType.NAME
                    )
                )
            )
            onArtistEvent(
                ArtistEvent.SetSortDirection(
                    getInt(
                        SoulSearchingSettings.SORT_ARTISTS_DIRECTION_KEY, SortDirection.ASC
                    )
                )
            )
            onArtistEvent(
                ArtistEvent.SetSortType(
                    getInt(
                        SoulSearchingSettings.SORT_ARTISTS_TYPE_KEY, SortType.NAME
                    )
                )
            )
        }
    }
}