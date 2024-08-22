package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AlbumState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ArtistState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.PlaylistState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.search.composable.SearchType
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchAll(
    searchText: String,
    musicState: AllMusicsState,
    albumState: AlbumState,
    artistState: ArtistState,
    playlistState: PlaylistState,
    onSelectedMusicForBottomSheet: (Music) -> Unit,
    onSelectedAlbumForBottomSheet: (Album) -> Unit,
    onSelectedPlaylistForBottomSheet: (Playlist) -> Unit,
    onSelectedArtistForBottomSheet: (ArtistWithMusics) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToPlaylist: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        val foundedPlaylists = playlistState.playlists.filter {
            it.playlist.name.lowercase().contains(searchText.lowercase())
        }
        if (foundedPlaylists.isNotEmpty()) {
            stickyHeader {
                SearchType(title = strings.playlists)
            }
            items(foundedPlaylists) { playlistWithMusics ->
                LinearPreviewComposable(
                    title = playlistWithMusics.playlist.name,
                    text = strings.musics(playlistWithMusics.musicsNumber),
                    onClick = {
                        focusManager.clearFocus()
                        onPlaylistEvent(
                            PlaylistEvent.SetSelectedPlaylist(
                                playlistWithMusics.playlist
                            )
                        )
                        navigateToPlaylist(playlistWithMusics.playlist.playlistId.toString())
                    },
                    onLongClick = {
                        onSelectedPlaylistForBottomSheet(playlistWithMusics.playlist)
                    },
                    coverId = playlistWithMusics.playlist.coverId,
                )
            }
        }

        val foundedArtists = artistState.artists.filter {
            it.artist.artistName.lowercase().contains(searchText.lowercase())
        }
        if (foundedArtists.isNotEmpty()) {
            stickyHeader {
                SearchType(title = strings.artists)
            }
            items(foundedArtists) { artistWithMusics ->
                LinearPreviewComposable(
                    title = artistWithMusics.artist.artistName,
                    text = strings.musics(artistWithMusics.musics.size),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToArtist(artistWithMusics.artist.artistId.toString())
                    },
                    onLongClick = {
                        onSelectedArtistForBottomSheet(artistWithMusics)
                    },
                    coverId = artistWithMusics.artist.coverId,
                )
            }
        }

        val foundedAlbums = albumState.albums.filter {
            it.artist?.artistName?.lowercase()?.contains(searchText.lowercase()) == true ||
                    it.album.albumName.lowercase().contains(searchText.lowercase())
        }
        if (foundedAlbums.isNotEmpty()) {
            stickyHeader {
                SearchType(title = strings.albums)
            }
            items(foundedAlbums) { albumWithArtist ->
                LinearPreviewComposable(
                    title = albumWithArtist.album.albumName,
                    text = if (albumWithArtist.artist != null) albumWithArtist.artist!!.artistName else "",
                    onClick = {
                        focusManager.clearFocus()
                        navigateToAlbum(albumWithArtist.album.albumId.toString())
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onSelectedAlbumForBottomSheet(albumWithArtist.album)
                        }
                    },
                    coverId = albumWithArtist.album.coverId,
                )
            }
        }

        val foundedMusics = musicState.musics.filter {
            it.name.lowercase().contains(searchText.lowercase())
                    || it.artist.lowercase().contains(searchText.lowercase())
                    || it.album.lowercase().contains(searchText.lowercase())

        }

        if (foundedMusics.isNotEmpty()) {
            stickyHeader {
                SearchType(title = strings.musics)
            }
            items(foundedMusics) { music ->
                MusicItemComposable(
                    music = music,
                    onClick = {
                        coroutineScope.launch {
                            focusManager.clearFocus()
                            playerViewManager.animateTo(
                                newState = BottomSheetStates.EXPANDED,
                            )
                        }.invokeOnCompletion {
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = music,
                                musicList = foundedMusics as ArrayList<Music>,
                                playlistId = null,
                                isMainPlaylist = isMainPlaylist,
                                isForcingNewPlaylist = true
                            )
                        }
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onSelectedMusicForBottomSheet(music)
                        }
                    },
                    isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(music.musicId)
                )
            }
        }
        item {
            SoulPlayerSpacer()
        }
    }
}
