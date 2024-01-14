package com.github.soulsearching.composables.search

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.LinearPreviewComposable
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.AlbumState
import com.github.soulsearching.states.ArtistState
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.strings
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchAll(
    searchText: String,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    musicState: MusicState,
    albumState: AlbumState,
    artistState: ArtistState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    onArtistEvent: (ArtistEvent) -> Unit,
    onAlbumEvent: (AlbumEvent) -> Unit,
    navigateToPlaylist: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    isMainPlaylist: Boolean,
    focusManager: FocusManager
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
            if (it.artist != null) {
                it.artist!!.artistName.lowercase().contains(searchText.lowercase())
                it.album.albumName.lowercase().contains(searchText.lowercase())
            } else {
                it.album.albumName.lowercase().contains(searchText.lowercase())
            }
        }
        if (foundedAlbums.isNotEmpty()) {
            stickyHeader {
                SearchType(title = strings.albums)
            }
            items(foundedAlbums) { album ->
                LinearPreviewComposable(
                    title = album.album.albumName,
                    text = if (album.artist != null) album.artist!!.artistName else "",
                    onClick = {
                        focusManager.clearFocus()
                        navigateToAlbum(album.album.albumId.toString())
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onAlbumEvent(
                                AlbumEvent.SetSelectedAlbum(
                                    album
                                )
                            )
                            onAlbumEvent(
                                AlbumEvent.BottomSheet(
                                    isShown = true
                                )
                            )
                        }
                    },
                    cover = retrieveCoverMethod(album.album.coverId)
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
                            playerDraggableState.animateTo(
                                BottomSheetStates.EXPANDED,
                                tween(Constants.AnimationDuration.normal)
                            )
                        }.invokeOnCompletion {
                            PlayerUtils.playerViewModel.handler.setCurrentPlaylistAndMusic(
                                music = music,
                                playlist = foundedMusics as ArrayList<Music>,
                                playlistId = null,
                                isMainPlaylist = isMainPlaylist,
                                isForcingNewPlaylist = true
                            )
                            playerMusicListViewModel.handler.savePlayerMusicList(PlayerUtils.playerViewModel.handler.currentPlaylist.map { it.musicId } as ArrayList<UUID>)
                        }
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onMusicEvent(
                                MusicEvent.SetSelectedMusic(
                                    music
                                )
                            )
                            onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = true
                                )
                            )
                        }
                    },
                    musicCover = retrieveCoverMethod(music.coverId)
                )
            }
        }
        item {
            PlayerSpacer()
        }
    }
}