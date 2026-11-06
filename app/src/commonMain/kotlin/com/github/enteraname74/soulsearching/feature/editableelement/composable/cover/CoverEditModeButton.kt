package com.github.enteraname74.soulsearching.feature.editableelement.composable.cover

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.grid_cover_type
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.simple_cover_type
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

@Composable
fun CoverEditModeButton(
    coverEditMode: CoverEditMode,
    onSwitchModeType: (new: CoverEditMode.Type) -> Unit,
) {
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
                        activeBorderColor = SoulSearchingColorTheme.colorScheme.text,
                        activeContainerColor = SoulSearchingColorTheme.colorScheme.surface,
                        activeContentColor = SoulSearchingColorTheme.colorScheme.text,
                        inactiveContainerColor = SoulSearchingColorTheme.colorScheme.surface,
                        inactiveContentColor = Color.Transparent,
                        inactiveBorderColor = SoulSearchingColorTheme.colorScheme.text,
                    ),
                    label = {
                        SoulText(
                            text = type.label(),
                            color = SoulSearchingColorTheme.colorScheme.text,
                            style = UiConstants.Typography.bodySmall,
                        )
                    },
                )
            }
        }
    }
}

private fun CoverEditMode.Type.label(): SoulTextSpec =
    when (this) {
        CoverEditMode.Type.Simple -> CoreRes.string.simple_cover_type.toTextSpec()
        CoverEditMode.Type.Grid -> CoreRes.string.grid_cover_type.toTextSpec()
    }