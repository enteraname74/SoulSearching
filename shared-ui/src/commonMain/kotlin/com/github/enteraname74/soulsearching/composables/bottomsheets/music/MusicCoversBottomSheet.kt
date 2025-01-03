package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.composables.StartTopBar
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoverSelectionItem
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import java.util.*

class MusicCoversBottomSheet(
    private val musicCover: Cover,
    private val onMusicFileCoverSelected: (path: String) -> Unit,
    private val onFileCoverSelected: (coverId: UUID) -> Unit,
    private val onCoverFromStorageSelected: (imageFile: PlatformFile) -> Unit,
    private val onClose: () -> Unit,
): SoulBottomSheet {

    @Composable
    private fun CoverFileList(
        coverFile: Cover.CoverFile,
        closeWithAnim: () -> Unit,
    ) {
        LazyVerticalGrid(
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
                            closeWithAnim()
                            onMusicFileCoverSelected(initialCoverPath)
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
                            closeWithAnim()
                            onFileCoverSelected(fileCoverId)
                        }
                    )
                }
            }
        }
    }

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
        ) { closeWithAnim ->

            val imagePickerLauncher = rememberFilePickerLauncher(
                type = PickerType.Image,
            ) { file ->
                if (file == null) return@rememberFilePickerLauncher
                onCoverFromStorageSelected(file)
            }

            Column(
                modifier = Modifier
                    .padding(
                        all = UiConstants.Spacing.medium,
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    UiConstants.Spacing.medium,
                )
            ) {
                StartTopBar(
                    title = strings.coverSelection,
                    rightAction = object : TopBarActionSpec {
                        override val icon: ImageVector = Icons.Rounded.AddPhotoAlternate
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