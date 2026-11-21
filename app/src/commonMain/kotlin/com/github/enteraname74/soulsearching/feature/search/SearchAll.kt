package com.github.enteraname74.soulsearching.feature.search

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.soulsearching.domain.model.AlbumPreview
import com.github.enteraname74.soulsearching.domain.model.ArtistPreview
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.PlaylistPreview
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.SearchAllState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.search.composable.SearchType
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchAll(
    lazyListState: LazyListState,
    searchAllState: SearchAllState,
    onSelectedMusicForBottomSheet: (musicId: Uuid) -> Unit,
    onSelectedAlbumForBottomSheet: (AlbumPreview) -> Unit,
    onSelectedPlaylistForBottomSheet: (PlaylistPreview) -> Unit,
    onSelectedArtistForBottomSheet: (ArtistPreview) -> Unit,
    navigateToPlaylist: (Uuid) -> Unit,
    navigateToArtist: (Uuid) -> Unit,
    navigateToAlbum: (Uuid) -> Unit,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

    LazyColumnCompat(
        state = lazyListState,
    ) {
        if (searchAllState.playlists.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_PLAYLIST_STICKY_KEY,
                contentType = SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = CoreRes.string.playlists.toTextSpec(),
                )
            }
            items(
                items = searchAllState.playlists,
                key = { it.id },
                contentType = { SEARCH_ALL_PLAYLISTS_CONTENT_TYPE }
            ) { playlistPreview ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    title = playlistPreview.name.toTextSpec(),
                    text = SoulTexts.musics(playlistPreview.totalMusics),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToPlaylist(playlistPreview.id)
                    },
                    onLongClick = {
                        onSelectedPlaylistForBottomSheet(playlistPreview)
                    },
                    cover = playlistPreview.cover,
                )
            }
        }

        if (searchAllState.artists.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_ARTIST_STICKY_KEY,
                contentType = SEARCH_ALL_ARTIST_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = CoreRes.string.artists.toTextSpec(),
                )
            }
            items(
                items = searchAllState.artists,
                key = { it.id },
                contentType = { SEARCH_ALL_ARTIST_CONTENT_TYPE }
            ) { artistPreview ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    title = artistPreview.name.toTextSpec(),
                    text = SoulTexts.musics(artistPreview.totalMusics),
                    onClick = {
                        focusManager.clearFocus()
                        navigateToArtist(artistPreview.id)
                    },
                    onLongClick = {
                        onSelectedArtistForBottomSheet(artistPreview)
                    },
                    cover = artistPreview.cover,
                )
            }
        }

        if (searchAllState.albums.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_ALBUM_STICKY_KEY,
                contentType = SEARCH_ALL_ALBUM_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = CoreRes.string.albums.toTextSpec(),
                )
            }
            items(
                items = searchAllState.albums,
                key = { it.id },
                contentType = { SEARCH_ALL_ALBUM_CONTENT_TYPE },
            ) { albumPreview ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    title = albumPreview.name.toTextSpec(),
                    text = albumPreview.artist.toTextSpec(),
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

        if (searchAllState.musics.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_ALL_MUSIC_STICKY_KEY,
                contentType = SEARCH_ALL_MUSIC_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = CoreRes.string.musics.toTextSpec(),
                )
            }
            items(
                items = searchAllState.musics,
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
                                musicList = searchAllState.musics,
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
                            onSelectedMusicForBottomSheet(music.musicId)
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
