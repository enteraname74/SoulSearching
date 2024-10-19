package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllAlbumsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllArtistsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllPlaylistsState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.search.composable.SearchType
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchAll(
    searchText: String,
    musicState: AllMusicsState,
    allAlbumsState: AllAlbumsState,
    allArtistsState: AllArtistsState,
    allPlaylistsState: AllPlaylistsState,
    onSelectedMusicForBottomSheet: (Music) -> Unit,
    onSelectedAlbumForBottomSheet: (Album) -> Unit,
    onSelectedPlaylistForBottomSheet: (Playlist) -> Unit,
    onSelectedArtistForBottomSheet: (ArtistWithMusics) -> Unit,
    navigateToPlaylist: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

    LazyColumn {
        val foundedPlaylists = allPlaylistsState.playlists.filter {
            it.playlist.name.lowercase().contains(searchText.lowercase())
        }
        if (foundedPlaylists.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_PLAYLIST_STICKY_KEY,
                contentType = SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItemPlacement(),
                    title = strings.playlists,
                )
            }
            items(
                items = foundedPlaylists,
                key = { it.playlist.playlistId },
                contentType = { SEARCH_ALL_PLAYLISTS_CONTENT_TYPE }
            ) { playlistWithMusics ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItemPlacement(),
                    title = playlistWithMusics.playlist.name,
                    text = strings.musics(playlistWithMusics.musicsNumber),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToPlaylist(playlistWithMusics.playlist.playlistId.toString())
                    },
                    onLongClick = {
                        onSelectedPlaylistForBottomSheet(playlistWithMusics.playlist)
                    },
                    cover = playlistWithMusics.cover,
                )
            }
        }

        val foundedArtists = allArtistsState.artists.filter {
            it.artist.artistName.lowercase().contains(searchText.lowercase())
        }
        if (foundedArtists.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_ARTIST_STICKY_KEY,
                contentType = SEARCH_ALL_ARTIST_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItemPlacement(),
                    title = strings.artists,
                )
            }
            items(
                items = foundedArtists,
                key = { it.artist.artistId },
                contentType = { SEARCH_ALL_ARTIST_CONTENT_TYPE }
            ) { artistWithMusics ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItemPlacement(),
                    title = artistWithMusics.artist.artistName,
                    text = strings.musics(artistWithMusics.musics.size),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToArtist(artistWithMusics.artist.artistId.toString())
                    },
                    onLongClick = {
                        onSelectedArtistForBottomSheet(artistWithMusics)
                    },
                    cover = artistWithMusics.cover,
                )
            }
        }

        val foundedAlbums = allAlbumsState.albums.filter {
            it.artist?.artistName?.lowercase()?.contains(searchText.lowercase()) == true ||
                    it.album.albumName.lowercase().contains(searchText.lowercase())
        }
        if (foundedAlbums.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_ALBUM_STICKY_KEY,
                contentType = SEARCH_ALL_ALBUM_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItemPlacement(),
                    title = strings.albums,
                )
            }
            items(
                items = foundedAlbums,
                key = { it.album.albumId },
                contentType = { SEARCH_ALL_ALBUM_CONTENT_TYPE },
            ) { albumWithArtist ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItemPlacement(),
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
                    cover = albumWithArtist.cover,
                )
            }
        }

        val foundedMusics = musicState.musics.filter {
            it.name.lowercase().contains(searchText.lowercase())
                    || it.artist.lowercase().contains(searchText.lowercase())
                    || it.album.lowercase().contains(searchText.lowercase())

        }

        if (foundedMusics.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_MUSIC_STICKY_KEY,
                contentType = SEARCH_ALL_MUSIC_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItemPlacement(),
                    title = strings.musics,
                )
            }
            items(
                items = foundedMusics,
                key = { it.musicId },
                contentType = { SEARCH_ALL_MUSIC_CONTENT_TYPE }
            ) { music ->
                MusicItemComposable(
                    modifier = Modifier
                        .animateItemPlacement(),
                    music = music,
                    onClick = {
                        coroutineScope.launch {
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = music,
                                musicList = foundedMusics as ArrayList<Music>,
                                playlistId = null,
                                isMainPlaylist = isMainPlaylist,
                                isForcingNewPlaylist = true
                            )
                            focusManager.clearFocus()
                            playerViewManager.animateTo(
                                newState = BottomSheetStates.EXPANDED,
                            )
                        }
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onSelectedMusicForBottomSheet(music)
                        }
                    },
                    isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
                )
            }
        }
        item(
            key = SEARCH_ALL_SPACER_KEY,
            contentType = SEARCH_ALL_SPACER_CONTENT_TYPE,
        ) {
            SoulPlayerSpacer()
        }
    }
}

private const val SEARCH_ALL_PLAYLIST_STICKY_KEY = "SEARCH_ALL_PLAYLIST_STICKY_KEY"
private const val SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE = "SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE"
private const val SEARCH_ALL_PLAYLISTS_CONTENT_TYPE = "SEARCH_ALL_PLAYLISTS_CONTENT_TYPE"

private const val SEARCH_ALL_ARTIST_STICKY_KEY = "SEARCH_ALL_ARTIST_STICKY_KEY"
private const val SEARCH_ALL_ARTIST_STICKY_CONTENT_TYPE = "SEARCH_ALL_ARTIST_STICKY_CONTENT_TYPE"
private const val SEARCH_ALL_ARTIST_CONTENT_TYPE = "SEARCH_ALL_ARTIST_CONTENT_TYPE"

private const val SEARCH_ALL_ALBUM_STICKY_KEY = "SEARCH_ALL_ALBUM_STICKY_KEY"
private const val SEARCH_ALL_ALBUM_STICKY_CONTENT_TYPE = "SEARCH_ALL_ALBUM_STICKY_CONTENT_TYPE"
private const val SEARCH_ALL_ALBUM_CONTENT_TYPE = "SEARCH_ALL_ALBUM_CONTENT_TYPE"

private const val SEARCH_ALL_MUSIC_STICKY_KEY = "SEARCH_ALL_MUSIC_STICKY_KEY"
private const val SEARCH_ALL_MUSIC_STICKY_CONTENT_TYPE = "SEARCH_ALL_MUSIC_STICKY_CONTENT_TYPE"
private const val SEARCH_ALL_MUSIC_CONTENT_TYPE = "SEARCH_ALL_MUSIC_CONTENT_TYPE"

private const val SEARCH_ALL_SPACER_KEY = "SEARCH_ALL_SPACER_KEY"
private const val SEARCH_ALL_SPACER_CONTENT_TYPE = "SEARCH_ALL_SPACER_CONTENT_TYPE"
