package com.github.soulsearching.classes

import android.content.SharedPreferences
import android.util.Log
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedPrefUtils {
    companion object {
        lateinit var sharedPreferences : SharedPreferences

        fun updateSort(keyToUpdate: String, newValue: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                with(sharedPreferences.edit()) {
                    putInt(keyToUpdate, newValue)
                    apply()
                }
            }
        }

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

        const val SORT_MUSICS_TYPE_KEY = "SORT_MUSICS_TYPE"
        const val SORT_MUSICS_DIRECTION_KEY = "SORT_MUSICS_DIRECTION"

        const val SORT_ALBUMS_TYPE_KEY = "SORT_ALBUMS_TYPE"
        const val SORT_ALBUMS_DIRECTION_KEY = "SORT_ALBUMS_DIRECTION"

        const val SORT_ARTISTS_TYPE_KEY = "SORT_ARTISTS_TYPE"
        const val SORT_ARTISTS_DIRECTION_KEY = "SORT_ARTISTS_DIRECTION"

        const val SORT_PLAYLISTS_TYPE_KEY = "SORT_PLAYLISTS_TYPE"
        const val SORT_PLAYLISTS_DIRECTION_KEY = "SORT_PLAYLISTS_DIRECTION"
    }
}