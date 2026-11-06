package com.github.enteraname74.soulsearching.feature.editableelement.composable.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

@Composable
fun EditableElementCoverSection(
    title: SoulTextSpec,
    coverEditMode: CoverEditMode,
    onSelectImage: (pos: Int) -> Unit,
    onSwitchModeType: (new: CoverEditMode.Type) -> Unit,
    coverSize: Dp,
    modifier: Modifier = Modifier,
) {

    if (isLargeWindow()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(
                UiConstants.Spacing.mediumPlus,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            ) {
                SoulText(
                    modifier = Modifier
                        .weight(1f),
                    text = title,
                    color = SoulSearchingColorTheme.colorScheme.text,
                    style = UiConstants.Typography.bodyTitle,
                )
                CoverEditModeButton(
                    coverEditMode = coverEditMode,
                    onSwitchModeType = onSwitchModeType,
                )
            }
            CoverEditModeImage(
                coverEditMode = coverEditMode,
                onSelectImage = onSelectImage,
                coverSize = coverSize,
                shape = RoundedCornerShape(size = UiConstants.Spacing.large),
            )
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(
                space = UiConstants.Spacing.medium,
                alignment = Alignment.CenterVertically,
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SoulText(
                text = title,
                color = SoulSearchingColorTheme.colorScheme.text
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            ) {
                CoverEditModeImage(
                    coverEditMode = coverEditMode,
                    onSelectImage = onSelectImage,
                    coverSize = coverSize,
                    shape = RoundedCornerShape(size = UiConstants.Spacing.mediumPlus),
                )
                CoverEditModeButton(
                    coverEditMode = coverEditMode,
                    onSwitchModeType = onSwitchModeType,
                )
            }
        }
    }
}
