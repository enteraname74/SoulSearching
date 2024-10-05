package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedIconButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedTextButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicFoldersState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
fun allMusicFoldersTab(
    mainPageViewModel: MainPageViewModel,
    navigateToFolder: (folderPath: String) -> Unit,
    showSoulMixDialog: () -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.FOLDERS,
    screen = {

        val folderState: AllMusicFoldersState by mainPageViewModel.allMusicFoldersState.collectAsState()

        MainPageList(
            list = folderState.allMusicFolders,
            title = strings.folders,
            innerComposable = {
                Buttons(
                    allMusicFolders = folderState.allMusicFolders,
                    onSeeSoulMixInformation = showSoulMixDialog,
                )
            },
            key = { it.path },
            contentType = { ALL_MUSIC_FOLDERS_CONTENT_TYPE },
            isUsingSort = false,
        ) { element ->
            BigPreviewComposable(
                modifier = Modifier
                    .animateItemPlacement(),
                cover = element.cover,
                title = element.path,
                text = strings.musics(total = element.musics.size),
                onClick = {
                    navigateToFolder(element.path)
                },
                onLongClick = { }
            )
        }
    }
)

@Composable
private fun Buttons(
    allMusicFolders: List<MusicFolderList>,
    onSeeSoulMixInformation: () -> Unit,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = UiConstants.Spacing.medium,
            ),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoulSegmentedButton(
                leftButtonSpec = SoulSegmentedTextButton(
                    data = strings.soulMix,
                    onClick = {
                        if (allMusicFolders.isEmpty()) return@SoulSegmentedTextButton
                        coroutineScope.launch {
                            playerViewManager.animateTo(
                                newState = BottomSheetStates.EXPANDED,
                            )
                        }.invokeOnCompletion {
                            playbackManager.playSoulMix(
                                musicLists = allMusicFolders.map { it.musics },
                            )
                        }
                    }
                ),
                rightButtonSpec = SoulSegmentedIconButton(
                    data = Icons.Rounded.Info,
                    onClick = onSeeSoulMixInformation,
                )
            )
            SoulIconButton(
                icon = Icons.Rounded.Shuffle,
                onClick = {
                    if (allMusicFolders.isEmpty()) return@SoulIconButton

                    coroutineScope.launch {
                        playerViewManager.animateTo(
                            newState = BottomSheetStates.EXPANDED,
                        )
                    }.invokeOnCompletion {
                        playbackManager.playShuffle(
                            musicList = buildList {
                                allMusicFolders.forEach { musicFolder ->
                                    addAll(musicFolder.musics)
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}

private const val ALL_MUSIC_FOLDERS_CONTENT_TYPE: String = "ALL_MUSIC_FOLDERS_CONTENT_TYPE"