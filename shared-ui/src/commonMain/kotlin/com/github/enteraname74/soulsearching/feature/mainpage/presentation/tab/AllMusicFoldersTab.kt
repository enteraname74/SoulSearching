package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

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
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.*
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicFoldersState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

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
                    .animateItem(),
                cover = element.cover,
                title = element.name,
                imageSize = null,
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
                buttons = listOf(
                    SoulSegmentedTextButton(
                        data = strings.soulMix,
                        contentPadding = SoulButtonDefaults.contentPadding(
                            horizontal = UiConstants.Spacing.medium,
                        ),
                        onClick = {
                            if (allMusicFolders.isEmpty()) return@SoulSegmentedTextButton
                            coroutineScope.launch {
                                playbackManager.playSoulMix(
                                    musicLists = allMusicFolders.map { it.musics },
                                )
                                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                            }
                        }
                    ),
                    SoulSegmentedIconButton(
                        data = Icons.Rounded.Info,
                        contentPadding = SoulButtonDefaults.contentPadding(
                            horizontal = 0.dp,
                        ),
                        onClick = onSeeSoulMixInformation,
                    )
                ),
            )
            SoulIconButton(
                icon = Icons.Rounded.Shuffle,
                onClick = {
                    if (allMusicFolders.isEmpty()) return@SoulIconButton

                    coroutineScope.launch {
                        playbackManager.playShuffle(
                            musicList = buildList {
                                allMusicFolders.forEach { musicFolder ->
                                    addAll(musicFolder.musics)
                                }
                            }
                        )
                        playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                    }
                }
            )
        }
    }
}

private const val ALL_MUSIC_FOLDERS_CONTENT_TYPE: String = "ALL_MUSIC_FOLDERS_CONTENT_TYPE"