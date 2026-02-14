package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedIconButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedTextButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicFoldersState
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import kotlinx.coroutines.flow.StateFlow

fun allMusicFoldersTab(
    state: StateFlow<AllMusicFoldersState>,
    navigateToFolder: (folderPath: String) -> Unit,
    showSoulMixDialog: () -> Unit,
    onSoulMixClicked: () -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.FOLDERS,
    screen = {
        val folderState: AllMusicFoldersState by state.collectAsState()

        MainPageList(
            list = folderState.allMusicFolders,
            title = strings.folders,
            rightComposable = {
                SoulMixButton(
                    allMusicFolders = folderState.allMusicFolders,
                    onSeeSoulMixInformation = showSoulMixDialog,
                    onSoulMixClicked = onSoulMixClicked,
                )
            },
            key = { it.folder },
            contentType = { ALL_MUSIC_FOLDERS_CONTENT_TYPE },
            isUsingSort = false,
        ) { element ->
            BigPreviewComposable(
                modifier = Modifier
                    .animateItem(),
                cover = element.cover,
                title = element.name,
                imageSize = null,
                text = strings.musics(total = element.totalMusics),
                onClick = {
                    navigateToFolder(element.folder)
                },
                onLongClick = { }
            )
        }
    }
)

@Composable
private fun SoulMixButton(
    allMusicFolders: List<MusicFolderPreview>,
    onSeeSoulMixInformation: () -> Unit,
    onSoulMixClicked: () -> Unit,
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
                    onSoulMixClicked()
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
}

private const val ALL_MUSIC_FOLDERS_CONTENT_TYPE: String = "ALL_MUSIC_FOLDERS_CONTENT_TYPE"