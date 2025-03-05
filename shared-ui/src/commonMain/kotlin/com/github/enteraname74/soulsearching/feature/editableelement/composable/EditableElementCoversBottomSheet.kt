package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.list.LazyVerticalGridCompat
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeight
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.StateFlow

class EditableElementCoversBottomSheet(
    private val coverStateFlow: StateFlow<CoverListState>,
    private val title: @Composable () -> String,
    private val onCoverSelected: (ByteArray) -> Unit,
    private val onCoverFromStorageSelected: (imageFile: PlatformFile) -> Unit,
    private val onClose: () -> Unit,
) : SoulBottomSheet {

    @Composable
    override fun BottomSheet() {
        val coverState: CoverListState by coverStateFlow.collectAsState()

        val windowHeightBeforeBarPadding: Float = rememberWindowHeight() - getStatusBarPadding()
        var bottomSheetHeight: Int by rememberSaveable {
            mutableIntStateOf(0)
        }

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
                TopBar(
                    title = title(),
                    modifier = if (bottomSheetHeight > windowHeightBeforeBarPadding) {
                        Modifier.statusBarsPadding()
                    } else {
                        Modifier
                    },
                    leftAction = TopBarNavigationAction(
                        onClick = closeWithAnim,
                    ),
                    rightAction = object : TopBarActionSpec {
                        override val icon: ImageVector = Icons.Rounded.AddPhotoAlternate
                        override val onClick: () -> Unit = {
                            imagePickerLauncher.launch()
                        }
                    }
                )

                LazyVerticalGridCompat(
                    modifier = Modifier.fillMaxWidth(),
                    columns = GridCells.Adaptive(UiConstants.ImageSize.largePlus),
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                ) {
                    editableElementCoversChoice(
                        coverState = coverState,
                        onCoverSelected = {
                            closeWithAnim()
                            onCoverSelected(it)
                        },
                        sectionTitle = null,
                    )
                }
            }
        }
    }

    @Composable
    private fun TopBar(
        modifier: Modifier,
        title: String,
        leftAction: TopBarActionSpec,
        rightAction: TopBarActionSpec,
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoulIconButton(
                icon = leftAction.icon,
                contentDescription = leftAction.contentDescription,
                onClick = leftAction.onClick,
                enabled = leftAction.isEnabled,
                colors = SoulButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                ),
            )
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                style = UiConstants.Typography.bodyMediumTitle,
                overflow = TextOverflow.Ellipsis,
            )
            SoulIconButton(
                icon = rightAction.icon,
                contentDescription = rightAction.contentDescription,
                onClick = rightAction.onClick,
                enabled = rightAction.isEnabled,
                colors = SoulButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                ),
            )
        }
    }
}