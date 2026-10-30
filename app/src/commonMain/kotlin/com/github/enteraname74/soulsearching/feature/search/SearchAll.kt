package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.albums
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.artists
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.collections
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.musics
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.playlists
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.feature.search.all.SearchAllState
import com.github.enteraname74.soulsearching.feature.search.composable.LinearPreviewComposable
import com.github.enteraname74.soulsearching.feature.search.composable.SearchType
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchAll(
    lazyListState: LazyListState,
    searchAllState: SearchAllState,
    focusManager: FocusManager,
    playbackManager: PlaybackManager = injectElement(),
) {
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()
    val isLargeWindow = isLargeWindow()

    LazyColumnCompat(
        state = lazyListState,
        contentPadding = if (isLargeWindow) {
            PaddingValues(
                horizontal = UiConstants.Spacing.large
            )
        } else {
            PaddingValues(
                horizontal = UiConstants.Spacing.medium
            )
        }
    ) {
        if (
            searchAllState.collections.isNotEmpty()
            && searchAllState.selectedSearchableElements.contains(SearchAllState.SearchableElement.Collections)
        ) {
            stickyHeader(
                key = SEARCH_ALL_COLLECTION_STICKY_KEY,
                contentType = SEARCH_ALL_COLLECTION_STICKY_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = CoreRes.string.collections.toTextSpec(),
                )
            }
            items(
                items = searchAllState.collections,
                key = { it.id },
                contentType = { SEARCH_ALL_COLLECTION_CONTENT_TYPE }
            ) { collectionPreview ->
                LinearPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    title = collectionPreview.name.toTextSpec(),
                    text = SoulTexts.musics(collectionPreview.totalMusics),
                    onClick = {
                        focusManager.clearFocus()
                        searchAllState.navigateToCollection(collectionPreview.id)
                    },
                    onLongClick = {
                        searchAllState.showCollectionBottomSheet(collectionPreview.id)
                    },
                    cover = collectionPreview.cover,
                )
            }
        }

        if (
            searchAllState.playlists.isNotEmpty()
            && searchAllState.selectedSearchableElements.contains(SearchAllState.SearchableElement.Playlists)
        ) {
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
                        searchAllState.navigateToPlaylist(playlistPreview.id)
                    },
                    onLongClick = {
                        searchAllState.showPlaylistBottomSheet(playlistPreview.id)
                    },
                    cover = playlistPreview.cover,
                )
            }
        }

        if (
            searchAllState.artists.isNotEmpty()
            && searchAllState.selectedSearchableElements.contains(SearchAllState.SearchableElement.Artists)
        ) {
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
                        searchAllState.navigateToArtist(artistPreview.id)
                    },
                    onLongClick = {
                        searchAllState.showArtistBottomSheet(artistPreview.id)
                    },
                    cover = artistPreview.cover,
                )
            }
        }

        if (
            searchAllState.albums.isNotEmpty()
            && searchAllState.selectedSearchableElements.contains(SearchAllState.SearchableElement.Albums)
        ) {
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
                        searchAllState.navigateToAlbum(albumPreview.id)
                    },
                    onLongClick = {
                        searchAllState.showAlbumBottomSheet(albumPreview.id)
                    },
                    cover = albumPreview.cover,
                )
            }
        }

        if (
            searchAllState.musics.isNotEmpty()
            && searchAllState.selectedSearchableElements.contains(SearchAllState.SearchableElement.Musics)
        ) {
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
                        focusManager.clearFocus()
                        searchAllState.onMusicClick(music)
                    },
                    onMoreClicked = {
                        searchAllState.showMusicBottomSheet(music.musicId)
                    },
                    isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
                )
            }
        }
        if (!isLargeWindow) {
            item(
                key = SEARCH_ALL_SPACER_KEY,
                contentType = SEARCH_ALL_SPACER_CONTENT_TYPE,
            ) {
                SoulPlayerSpacer()
            }
        }
    }
}

private const val SEARCH_ALL_PLAYLIST_STICKY_KEY = "SEARCH_ALL_PLAYLIST_STICKY_KEY"
private const val SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE =
    "SEARCH_ALL_PLAYLIST_STICKY_CONTENT_TYPE"
private const val SEARCH_ALL_PLAYLISTS_CONTENT_TYPE = "SEARCH_ALL_PLAYLISTS_CONTENT_TYPE"

private const val SEARCH_ALL_COLLECTION_STICKY_KEY = "SEARCH_ALL_COLLECTION_STICKY_KEY"
private const val SEARCH_ALL_COLLECTION_STICKY_CONTENT_TYPE =
    "SEARCH_ALL_COLLECTION_STICKY_CONTENT_TYPE"
private const val SEARCH_ALL_COLLECTION_CONTENT_TYPE = "SEARCH_ALL_COLLECTION_CONTENT_TYPE"

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
