package com.github.enteraname74.soulsearching.feature.search.musics.composable

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
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.musics
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.feature.search.composable.SearchType
import com.github.enteraname74.soulsearching.feature.search.musics.SearchMusicsState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchMusics(
    lazyListState: LazyListState,
    state: SearchMusicsState,
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
        if (state.musics.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_MUSIC_STICKY_HEADER_KEY,
                contentType = SEARCH_MUSIC_STICKY_HEADER_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = CoreRes.string.musics.toTextSpec(),
                )
            }
        }
        items(
            key = { it.musicId },
            contentType = { SEARCH_MUSIC_CONTENT_TYPE },
            items = state.musics,
        ) { music ->
            MusicItemComposable(
                modifier = Modifier
                    .animateItem(),
                music = music,
                onClick = { selectedMusic ->
                    focusManager.clearFocus()
                    state.onMusicClick(selectedMusic)
                },
                onMoreClicked = {
                    state.showMusicBottomSheet(music.musicId)
                },
                isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
            )
        }
        if (!isLargeWindow) {
            item(
                key = SEARCH_MUSIC_SPACER_KEY,
                contentType = SEARCH_MUSIC_SPACER_CONTENT_TYPE,
            ) {
                SoulPlayerSpacer()
            }
        }
    }
}

private const val SEARCH_MUSIC_STICKY_HEADER_KEY: String = "SEARCH_MUSIC_STICKY_HEADER_KEY"
private const val SEARCH_MUSIC_STICKY_HEADER_CONTENT_TYPE: String = "SEARCH_MUSIC_STICKY_HEADER_CONTENT_TYPE"
private const val SEARCH_MUSIC_CONTENT_TYPE: String = "SEARCH_MUSIC_CONTENT_TYPE"
private const val SEARCH_MUSIC_SPACER_KEY: String = "SEARCH_MUSIC_SPACER_KEY"
private const val SEARCH_MUSIC_SPACER_CONTENT_TYPE: String = "SEARCH_MUSIC_SPACER_CONTENT_TYPE"
