package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.events.ArtistEvent
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AlbumState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ArtistState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageState
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
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    musicState: MainPageState,
    albumState: AlbumState,
    artistState: ArtistState,
    playlistState: PlaylistState,
    onSelectedMusicForBottomSheet: (Music) -> Unit,
    onSelectedAlbumForBottomSheet: (Album) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    onArtistEvent: (ArtistEvent) -> Unit,
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
            items(foundedPlaylists) { playlist ->
                LinearPreviewComposable(
                    title = playlist.playlist.name,
                    text = strings.musics(playlist.musicsNumber),
                    onClick = {
                        focusManager.clearFocus()
                        onPlaylistEvent(
                            PlaylistEvent.SetSelectedPlaylist(
                                playlist.playlist
                            )
                        )
                        navigateToPlaylist(playlist.playlist.playlistId.toString())
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onPlaylistEvent(
                                PlaylistEvent.SetSelectedPlaylist(
                                    playlist.playlist
                                )
                            )
                            onPlaylistEvent(
                                PlaylistEvent.BottomSheet(
                                    isShown = true
                                )
                            )
                        }
                    },
                    cover = retrieveCoverMethod(playlist.playlist.coverId)
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
            items(foundedArtists) { artist ->
                LinearPreviewComposable(
                    title = artist.artist.artistName,
                    text = strings.musics(artist.musics.size),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToArtist(artist.artist.artistId.toString())
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onArtistEvent(
                                ArtistEvent.SetSelectedArtistWithMusics(
                                    artist
                                )
                            )
                            onArtistEvent(
                                ArtistEvent.BottomSheet(
                                    isShown = true
                                )
                            )
                        }
                    },
                    cover = retrieveCoverMethod(artist.artist.coverId)
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
                    cover = retrieveCoverMethod(albumWithArtist.album.coverId)
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
                    musicCover = retrieveCoverMethod(music.coverId),
                    isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(music.musicId)
                )
            }
        }
        item {
            SoulPlayerSpacer()
        }
    }
}
