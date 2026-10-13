package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.composables.image.SoulByteArrayImage
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_add_photo_alternate_filled
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.SoulPlatform
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode
import com.github.enteraname74.soulsearching.feature.editableelement.domain.GridUpdatedCells

@Composable
internal fun CoverEditModeImage(
    coverEditMode: CoverEditMode,
    onSwitchModeType: (new: CoverEditMode.Type) -> Unit,
    onSelectImage: (pos: Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        when {
            coverEditMode.selectedType == CoverEditMode.Type.Grid && coverEditMode.grid != null -> Grid(
                coverEditMode = coverEditMode.grid,
                onSelectImage = onSelectImage,
            )
            else -> Simple(
                coverEditMode = coverEditMode.simple,
                onSelectImage = onSelectImage,
            )
        }

        if (coverEditMode.selectionAllowed) {
            SingleChoiceSegmentedButtonRow {
                CoverEditMode.Type.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = type == coverEditMode.selectedType,
                        onClick = { onSwitchModeType(type) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = CoverEditMode.Type.entries.size
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeBorderColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                            activeContainerColor = SoulSearchingColorTheme.colorScheme.secondary,
                            activeContentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                            inactiveContainerColor = SoulSearchingColorTheme.colorScheme.secondary,
                            inactiveContentColor = Color.Transparent,
                            inactiveBorderColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                        ),
                        label = {
                            Text(
                                text = type.label(),
                                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                                style = UiConstants.Typography.bodySmall,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun Simple(
    coverEditMode: CoverEditMode.Simple,
    onSelectImage: (pos: Int) -> Unit,
) {
    // TODO WEB: Add support for cover update
    if (coverEditMode.newCover == null) {
        SoulImage(
            cover = coverEditMode.initialCover,
            size = UiConstants.CoverSize.huge,
            modifier = Modifier.clickableWithHandCursor(
                enabled = SoulPlatformUtils.platform != SoulPlatform.Web
            ) {
                onSelectImage(0)
            }
        )
    } else {
        SoulByteArrayImage(
            data = coverEditMode.newCover,
            size = UiConstants.CoverSize.huge,
            modifier = Modifier.clickableWithHandCursor(
                enabled = SoulPlatformUtils.platform != SoulPlatform.Web
            ) {
                onSelectImage(0)
            }
        )
    }
}

@Composable
private fun Grid(
    coverEditMode: CoverEditMode.Grid,
    onSelectImage: (pos: Int) -> Unit,
) {
    val isSelectionEnabled = SoulPlatformUtils.platform != SoulPlatform.Web
    val cells = coverEditMode.updatedCells()

    Column(
        modifier = Modifier
            .size(UiConstants.CoverSize.huge)
            .clip(RoundedCornerShape(percent = 10)),
    ) {
        repeat(2) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                repeat(2) { columnIndex ->
                    val cellIndex = rowIndex * 2 + columnIndex
                    val cell = cells[cellIndex]

                    GridCell(
                        cell = cell,
                        rowIndex = rowIndex,
                        columnIndex = columnIndex,
                        contentDescription = gridCellContentDescription(cellIndex),
                        selectionEnabled = isSelectionEnabled,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        onClick = { onSelectImage(cellIndex) },
                    )
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    cell: GridUpdatedCells,
    rowIndex: Int,
    columnIndex: Int,
    contentDescription: String,
    selectionEnabled: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val imageSize: Dp? = null

    Box(
        modifier = modifier
            .clickableWithHandCursor(
                enabled = selectionEnabled,
                onClick = onClick,
            )
            .clearAndSetSemantics {
                this.contentDescription = contentDescription
                role = Role.Button
                if (selectionEnabled) {
                    onClick {
                        onClick()
                        true
                    }
                } else {
                    disabled()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        when (cell) {
            is GridUpdatedCells.ExistingCell -> SoulImage(
                cover = cell.cover,
                size = imageSize,
                roundedPercent = 0,
                modifier = Modifier.fillMaxSize(),
            )

            is GridUpdatedCells.NewCell -> SoulByteArrayImage(
                data = cell.byteArray,
                size = imageSize,
                roundedPercent = 0,
                modifier = Modifier.fillMaxSize(),
            )

            GridUpdatedCells.Empty -> EmptyGridCell(
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }
}

@Composable
private fun EmptyGridCell(
    rowIndex: Int,
    columnIndex: Int,
) {
    val borderColor = SoulSearchingColorTheme.colorScheme.onSecondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.secondary)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val halfStroke = strokeWidth / 2f

                if (rowIndex == 0) {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height - halfStroke),
                        end = Offset(size.width, size.height - halfStroke),
                        strokeWidth = strokeWidth,
                    )
                } else {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, halfStroke),
                        end = Offset(size.width, halfStroke),
                        strokeWidth = strokeWidth,
                    )
                }

                if (columnIndex == 0) {
                    drawLine(
                        color = borderColor,
                        start = Offset(size.width - halfStroke, 0f),
                        end = Offset(size.width - halfStroke, size.height),
                        strokeWidth = strokeWidth,
                    )
                } else {
                    drawLine(
                        color = borderColor,
                        start = Offset(halfStroke, 0f),
                        end = Offset(halfStroke, size.height),
                        strokeWidth = strokeWidth,
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        SoulIcon(
            icon = CoreRes.drawable.ic_add_photo_alternate_filled,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            size = UiConstants.ImageSize.medium,
        )
    }
}

@Composable
private fun gridCellContentDescription(index: Int): String =
    when (index) {
        0 -> strings.selectTopStartCoverImage
        1 -> strings.selectTopEndCoverImage
        2 -> strings.selectBottomStartCoverImage
        3 -> strings.selectBottomEndCoverImage
        else -> error("Unsupported grid cell index: $index")
    }

private fun CoverEditMode.Type.label(): String =
    when (this) {
        CoverEditMode.Type.Simple -> strings.simpleCoverType
        CoverEditMode.Type.Grid -> strings.gridCoverType
    }
