package com.github.soulsearching.classes

import android.content.SharedPreferences
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

        fun setMusicsFetched(){
            CoroutineScope(Dispatchers.IO).launch {
                with(sharedPreferences.edit()) {
                    putBoolean(HAS_MUSICS_BEEN_FETCHED_KEY, true)
                    apply()
                }
            }
        }
        
        fun hasMusicsBeenFetched() : Boolean {
            return sharedPreferences.getBoolean(
                HAS_MUSICS_BEEN_FETCHED_KEY,
                false
            )
        }


        fun getPlayerSavedCurrentMusic() {
            val index = sharedPreferences.getInt(PLAYER_MUSIC_INDEX_KEY, -1)
            val position = sharedPreferences.getInt(PLAYER_MUSIC_POSITION_KEY, 0)

            PlayerUtils.playerViewModel.setMusicFromIndex(index)
            PlayerUtils.playerViewModel.currentMusicPosition = position
        }

        fun setPlayerSavedCurrentMusic() {
            CoroutineScope(Dispatchers.IO).launch {
                with(sharedPreferences.edit()) {
                    putInt(PLAYER_MUSIC_INDEX_KEY, PlayerUtils.playerViewModel.getIndexOfCurrentMusic())
                    apply()
                }
            }
            setCurrentMusicPosition()
        }

        fun setCurrentMusicPosition() {
            CoroutineScope(Dispatchers.IO).launch {
                with(sharedPreferences.edit()) {
                    putInt(PLAYER_MUSIC_POSITION_KEY, PlayerUtils.playerViewModel.currentMusicPosition)
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
    }
}