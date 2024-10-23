package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulMenuElement(
    title: String,
    subTitle: String,
    icon: ImageVector? = null,
    onClick: (() -> Unit)?,
    padding: PaddingValues = PaddingValues(
        horizontal = UiConstants.Spacing.large,
        vertical = UiConstants.Spacing.veryLarge,
    ),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .optionalClickable(onClick = onClick)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
    ) {
        icon?.let {
            Image(
                modifier = Modifier.size(UiConstants.ImageSize.medium),
                imageVector = it,
                contentDescription = "",
                colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onPrimary)
            )
        }
        SoulMenuBody(
            title = title,
            text = subTitle,
        )
    }
}