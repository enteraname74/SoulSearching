package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

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
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_info_filled
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicFoldersState
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import com.github.enteraname74.soulsearching.feature.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import kotlinx.coroutines.flow.StateFlow

fun allMusicFoldersTab(
    state: StateFlow<AllMusicFoldersState>,
    multiSelectionState: StateFlow<MultiSelectionState>,
    navigateToFolder: (folderPath: String) -> Unit,
    toggleFolderSelection: (folderPath: String, mode: SelectionMode) -> Unit,
    showSoulMixDialog: () -> Unit,
    onSoulMixClicked: () -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.FOLDERS,
    screen = {
        val folderState: AllMusicFoldersState by state.collectAsState()
        val selectionState: MultiSelectionState by multiSelectionState.collectAsState()

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
                onLongClick = {
                    toggleFolderSelection(element.folder, SelectionMode.Folder)
                },
                isSelected = selectionState.selectedIds.contains(element.folder),
                isSelectionModeOn = selectionState.totalSelected > 0,
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
                data = CoreRes.drawable.ic_info_filled,
                contentPadding = SoulButtonDefaults.contentPadding(
                    horizontal = 0.dp,
                ),
                onClick = onSeeSoulMixInformation,
            )
        ),
    )
}

private const val ALL_MUSIC_FOLDERS_CONTENT_TYPE: String = "ALL_MUSIC_FOLDERS_CONTENT_TYPE"
