package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulMenuSwitch(
    title: String,
    subTitle: String? = null,
    toggleAction: () -> Unit,
    isChecked: Boolean,
    maxLines: Int = 2,
    titleColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
    padding: PaddingValues = PaddingValues(
        horizontal = UiConstants.Spacing.large,
        vertical = UiConstants.Spacing.veryLarge,
    ),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithHandCursor {
                toggleAction()
            }
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
        ) {
            SoulMenuBody(
                title = title,
                text = subTitle,
                titleColor = titleColor,
                textColor = textColor,
                titleMaxLines = maxLines,
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { toggleAction() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = textColor,
                checkedTrackColor = titleColor,
                checkedBorderColor = titleColor,
                uncheckedThumbColor = textColor,
                uncheckedTrackColor = Color.Transparent,
                uncheckedBorderColor = textColor,
            )
        )
    }
}