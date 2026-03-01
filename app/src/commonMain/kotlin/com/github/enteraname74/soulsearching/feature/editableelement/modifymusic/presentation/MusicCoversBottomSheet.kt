package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_add_photo_alternate_filled
import com.github.enteraname74.soulsearching.coreui.list.LazyVerticalGridCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeight
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoverSelectionItem
import com.github.enteraname74.soulsearching.feature.editableelement.composable.editableElementCoversChoice
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.DrawableResource
import java.util.UUID

class MusicCoversBottomSheet(
    private val musicCover: Cover,
    private val albumCoversStateFlow: StateFlow<CoverListState>,
    private val onMusicFileCoverSelected: (path: String) -> Unit,
    private val onFileCoverSelected: (coverId: UUID) -> Unit,
    private val onAlbumCoverSelected: (ByteArray) -> Unit,
    private val onCoverFromStorageSelected: (imageFile: PlatformFile) -> Unit,
    private val onClose: () -> Unit,
): SoulBottomSheet {

    @Composable
    private fun CoverFileList(
        coverFile: Cover.CoverFile,
        closeWithAnim: (callback: () -> Unit) -> Unit,
    ) {
        val coverState: CoverListState by albumCoversStateFlow.collectAsState()

        LazyVerticalGridCompat(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(UiConstants.ImageSize.largePlus),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            coverFile.initialCoverPath?.let { initialCoverPath ->
                item {
                    EditableElementCoverSelectionItem(
                        cover = Cover.CoverFile(
                            initialCoverPath = initialCoverPath,
                        ),
                        title = strings.musicFileCover,
                        onClick = {
                            closeWithAnim {
                                onMusicFileCoverSelected(initialCoverPath)
                            }
                        }
                    )
                }
            }

            coverFile.fileCoverId?.let { fileCoverId ->
                item {
                    EditableElementCoverSelectionItem(
                        cover = Cover.CoverFile(
                            fileCoverId = fileCoverId,
                        ),
                        title = strings.musicAppCover,
                        onClick = {
                            closeWithAnim {
                                onFileCoverSelected(fileCoverId)
                            }
                        }
                    )
                }
            }

            editableElementCoversChoice(
                coverState = coverState,
                onCoverSelected = {
                    closeWithAnim {
                        onAlbumCoverSelected(it)
                    }
                },
                sectionTitle = strings.coversOfSongAlbum,
            )
        }
    }

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
        ) { closeWithAnim ->

            val imagePickerLauncher = rememberFilePickerLauncher(
                type = FileKitType.Image,
            ) { file ->
                if (file == null) return@rememberFilePickerLauncher
                closeWithAnim {
                    onCoverFromStorageSelected(file)
                }
            }

            val windowHeightBeforeBarPadding: Float = rememberWindowHeight() - getStatusBarPadding()
            var bottomSheetHeight: Int by rememberSaveable {
                mutableIntStateOf(0)
            }

            Column(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        bottomSheetHeight = layoutCoordinates.size.height
                    }
                    .padding(
                        all = UiConstants.Spacing.medium,
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    UiConstants.Spacing.medium,
                )
            ) {
                SoulTopBar(
                    title = strings.coverSelection,
                    colors = SoulTopBarDefaults.secondary(),
                    withStatusBarPadding = bottomSheetHeight > windowHeightBeforeBarPadding,
                    leftAction = TopBarNavigationAction(
                        onClick = { closeWithAnim { } },
                    ),
                    rightAction = object : TopBarActionSpec {
                        override val icon: DrawableResource = CoreRes.drawable.ic_add_photo_alternate_filled
                        override val onClick: () -> Unit = {
                            imagePickerLauncher.launch()
                        }
                    }
                )
                when (musicCover) {
                    is Cover.CoverFile -> {
                        CoverFileList(
                            coverFile = musicCover,
                            closeWithAnim = closeWithAnim,
                        )
                    }
                }
            }
        }
    }
}