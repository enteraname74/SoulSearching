package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.composables.StartTopBar
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.list.LazyVerticalGridCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoverSelectionItem
import com.github.enteraname74.soulsearching.feature.editableelement.composable.editableElementCoversChoice
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.StateFlow
import java.util.*

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
    private fun ImageList(
        block: LazyGridScope.() -> Unit,
    ) {
        LazyVerticalGridCompat(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(UiConstants.ImageSize.largePlus),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            block()
        }
    }

    private fun LazyGridScope.coverFileList(
        coverFile: Cover.CoverFile,
        closeWithAnim: () -> Unit,
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

    private fun LazyGridScope.coverUrlList(
        cover: Cover.CoverUrl,
        closeWithAnim: () -> Unit,
    ) {
        cover.url?.let { _ ->
            item {
                EditableElementCoverSelectionItem(
                    cover = cover,
                    title = strings.musicAppCover,
                    onClick = {
                        closeWithAnim()
                        // TODO: Select URL image on song update
                    }
                )
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
                closeWithAnim()
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

                val coverState: CoverListState by albumCoversStateFlow.collectAsState()

                ImageList {
                    when (musicCover) {
                        is Cover.CoverFile -> {
                            coverFileList(
                                coverFile = musicCover,
                                closeWithAnim = closeWithAnim,
                            )
                        }
                        is Cover.CoverUrl -> {
                            coverUrlList(
                                cover = musicCover,
                                closeWithAnim = closeWithAnim,
                            )
                        }
                    }

                    editableElementCoversChoice(
                        coverState = coverState,
                        onCoverSelected = {
                            closeWithAnim()
                            onAlbumCoverSelected(it)
                        },
                        sectionTitle = strings.coversOfSongAlbum,
                    )
                }
            }
        }
    }
}