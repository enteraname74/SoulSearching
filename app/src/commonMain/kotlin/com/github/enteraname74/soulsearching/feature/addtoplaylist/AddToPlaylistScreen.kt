package com.github.enteraname74.soulsearching.feature.addtoplaylist

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.EmptyCard
import com.github.enteraname74.soulsearching.feature.search.composable.SoulSearchBar
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

@Composable
internal fun AddToPlaylistScreen(
    state: AddToPlaylistState,
) {
    val musics: LazyPagingItems<Music> = state.musics.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    SoulScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SoulTopBar(
                title = CoreRes.string.add_to_playlist_screen_title.toTextSpec(),
                leftAction = TopBarNavigationAction(
                    onClick = state.navigateBack,
                ),
                rightAction = TopBarValidateAction(
                    onClick = state.onSave,
                )
            )
            SoulSearchBar(
                modifier = Modifier
                    .padding(
                        top = UiConstants.Spacing.medium,
                        start = UiConstants.Spacing.medium,
                        end = UiConstants.Spacing.medium,
                        bottom = UiConstants.Spacing.medium,
                    ),
                searchText = searchText,
                placeholder = CoreRes.string.add_to_playlist_search_placeholder.toTextSpec(),
                updateTextMethod = {
                    searchText = it
                    state.onSearch(it)
                    coroutineScope.launch { lazyListState.scrollToItem(0) }
                },
                focusManager = LocalFocusManager.current,
                focusRequester = focusRequester,
            )
            SoulText(
                modifier = Modifier
                    .padding(
                        start = UiConstants.Spacing.medium,
                        end = UiConstants.Spacing.medium,
                        bottom = UiConstants.Spacing.mediumPlus,
                    ),
                text = SoulTexts.selectedMusics(state.selectedMusicIds.size),
                color = SoulSearchingColorTheme.colorScheme.text,
                style = UiConstants.Typography.body,
            )
            LazyColumnCompat(
                modifier = Modifier
                    .fillMaxSize(),
                state = lazyListState,
            ) {
                if (musics.itemCount == 0) {
                    item {
                        EmptyCard(
                            modifier = Modifier
                                .widthIn(
                                    min = 0.dp,
                                    max = 500.dp
                                )
                                .padding(
                                    horizontal = UiConstants.Spacing.large,
                                )
                                .animateItem(),
                            title = CoreRes.string.add_to_playlist_empty_title.toTextSpec(),
                            description = CoreRes.string.add_to_playlist_empty_text.toTextSpec(),
                        )
                    }
                }
                items(
                    count = musics.itemCount,
                    key = { musics[it]?.musicId ?: Uuid.random() },
                    contentType = { ADD_TO_PLAYLIST_SONGS_CONTENT_TYPE }
                ) { pos ->
                    val music = musics[pos]
                    music?.let {
                        MusicItemComposable(
                            modifier = Modifier
                                .animateItem(),
                            music = music,
                            onClick = { state.onToggleSelection(it.musicId) },
                            onMoreClicked = null,
                            textColor = SoulSearchingColorTheme.colorScheme.text,
                            isPlayedMusic = false,
                            hideMoreIcon = true,
                            isSelected = state.selectedMusicIds.contains(music.musicId),
                        )
                    }
                }
                item { SoulPlayerSpacer() }
            }
        }
    }
}

private const val ADD_TO_PLAYLIST_SONGS_CONTENT_TYPE: String = "ADD_TO_PLAYLIST_SONGS_CONTENT_TYPE"