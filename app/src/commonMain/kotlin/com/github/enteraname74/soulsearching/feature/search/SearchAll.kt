package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
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
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchAll(
    searchText: String,
    musicState: AllMusicsState,
    allAlbumsState: AllAlbumsState,
    allArtistsState: AllArtistsState,
    allPlaylistsState: AllPlaylistsState,
    onSelectedMusicForBottomSheet: (Music) -> Unit,
    onSelectedAlbumForBottomSheet: (AlbumPreview) -> Unit,
    onSelectedPlaylistForBottomSheet: (PlaylistPreview) -> Unit,
    onSelectedArtistForBottomSheet: (ArtistPreview) -> Unit,
    navigateToPlaylist: (UUID) -> Unit,
    navigateToArtist: (UUID) -> Unit,
    navigateToAlbum: (UUID) -> Unit,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

    LazyColumnCompat {
        // TODO: Normalise with accents.
        // TODO OPTIMIZATION: Move search in viewModel.
        val foundedPlaylists = emptyList<PlaylistPreview>()
//        allPlaylistsState.playlists.filter {
//            it.name.lowercase().contains(searchText.lowercase())
//        }
        if (foundedPlaylists.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_PLAYLIST_STICKY_KEY,
                contentType = SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = strings.playlists,
                )
            }
            items(
                items = foundedPlaylists,
                key = { it.id },
                contentType = { SEARCH_ALL_PLAYLISTS_CONTENT_TYPE }
            ) { playlistWithMusics ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    title = playlistWithMusics.name,
                    text = strings.musics(playlistWithMusics.totalMusics),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToPlaylist(playlistWithMusics.id)
                    },
                    onLongClick = {
                        onSelectedPlaylistForBottomSheet(playlistWithMusics)
                    },
                    cover = playlistWithMusics.cover,
                )
            }
        }

        // TODO: Normalise with accents.
        // TODO OPTIMIZATION: Move search in viewModel.
        val foundedArtists = emptyList<ArtistPreview>()
//        allArtistsState.artists.filter {
//            it.artist.artistName.lowercase().contains(searchText.lowercase())
//        }
        if (foundedArtists.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_ARTIST_STICKY_KEY,
                contentType = SEARCH_ALL_ARTIST_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = strings.artists,
                )
            }
            items(
                items = foundedArtists,
                key = { it.id },
                contentType = { SEARCH_ALL_ARTIST_CONTENT_TYPE }
            ) { albumPreview ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    title = albumPreview.name,
                    text = strings.musics(albumPreview.totalMusics),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToArtist(albumPreview.id)
                    },
                    onLongClick = {
                        onSelectedArtistForBottomSheet(albumPreview)
                    },
                    cover = albumPreview.cover,
                )
            }
        }

        // TODO: Normalise with accents.
        // TODO OPTIMIZATION: Move search in viewModel.
        val foundedAlbums = emptyList<AlbumPreview>()
//        allAlbumsState.albums.filter {
//            it.album.artist.artistName.lowercase().contains(searchText.lowercase()) ||
//                    it.album.albumName.lowercase().contains(searchText.lowercase())
//        }
        if (foundedAlbums.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_ALBUM_STICKY_KEY,
                contentType = SEARCH_ALL_ALBUM_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = strings.albums,
                )
            }
            items(
                items = foundedAlbums,
                key = { it.id },
                contentType = { SEARCH_ALL_ALBUM_CONTENT_TYPE },
            ) { albumPreview ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    title = albumPreview.name,
                    text = albumPreview.artist,
                    onClick = {
                        focusManager.clearFocus()
                        navigateToAlbum(albumPreview.id)
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onSelectedAlbumForBottomSheet(albumPreview)
                        }
                    },
                    cover = albumPreview.cover,
                )
            }
        }

        // TODO: Normalise with accents.
        // TODO OPTIMIZATION: Move search in viewModel.
        val foundedMusics = emptyList<Music>()
//        musicState.musics.filter {
//            it.name.lowercase().contains(searchText.lowercase())
//                    || it.artistsNames.lowercase().contains(searchText.lowercase())
//                    || it.album.albumName.lowercase().contains(searchText.lowercase())
//
//        }

        if (foundedMusics.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_MUSIC_STICKY_KEY,
                contentType = SEARCH_ALL_MUSIC_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
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
                        .animateItem(),
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
                            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                        }
                    },
                    onMoreClicked = {
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
private const val SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE =
    "SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE"
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
