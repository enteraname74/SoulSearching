package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconColors
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PlayerListView(
    playbackManager: PlaybackManager = injectElement(),
    currentMusicIndex: Int,
    isExpanded: Boolean,
    playedList: List<Music>,
    onLongSelectOnMusic: (Music) -> Unit,
    onMoreClickedOnMusic: (Music) -> Unit,
    secondaryColor: Color,
    buttonColors: SoulButtonColors,
    multiSelectionState: MultiSelectionState,
    selectedIconColors: SoulSelectedIconColors,
) {

    val coroutineScope = rememberCoroutineScope()
    val playerListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UiConstants.Spacing.small),
                contentAlignment = Alignment.CenterEnd,
            ) {
                SoulButton(
                    colors = buttonColors,
                    onClick = {
                        coroutineScope.launch {
                            if (currentMusicIndex != -1) {
                                playerListState.animateScrollToItem(
                                    currentMusicIndex
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = strings.currentSong,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = buttonColors.contentColor,
                        fontSize = 12.sp
                    )
                }
            }

            val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

            LazyColumnCompat(
                state = playerListState,
                contentPadding = PaddingValues(
                    bottom = getNavigationBarPadding().toDp()
                )
            ) {
                items(
                    items = playedList,
                    key = { it.musicId },
                    contentType = { PLAYER_LIST_CONTENT_TYPE }
                ) { elt ->
                    MusicItemComposable(
                        modifier = Modifier
                            .animateItem(),
                        music = elt,
                        onClick = { music ->
                            CoroutineScope(Dispatchers.IO).launch {
                                playbackManager.setAndPlayMusic(music)
                            }
                        },
                        onMoreClicked = {
                            coroutineScope.launch {
                                onMoreClickedOnMusic(elt)
                            }
                        },
                        onLongClick = { onLongSelectOnMusic(elt) },
                        textColor = secondaryColor,
                        isPlayedMusic = currentPlayedSong?.musicId == elt.musicId,
                        isSelected = multiSelectionState.selectedIds.contains(elt.musicId),
                        isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                        selectedIconColors = selectedIconColors,
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = getNavigationBarPadding().toDp())
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = secondaryColor
                )
            }
        }
    }
}

private const val PLAYER_LIST_CONTENT_TYPE: String = "PLAYER_LIST_CONTENT_TYPE"