package com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingDarkLightTheme
import com.github.enteraname74.soulsearching.theme.isInDarkTheme

@Composable
fun SettingsThemeItem(
    theme: SoulSearchingDarkLightTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithHandCursor { onClick() }
            .padding(
                horizontal = UiConstants.Spacing.large,
                vertical = UiConstants.Spacing.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
        ) {
            ThemeIndicator(
                color = theme.palette(isInDarkTheme = isInDarkTheme()).secondary
            )
            Text(
                text = theme.name(),
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                unselectedColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
        )
    }
}

@Composable
private fun ThemeIndicator(
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(THEME_INDICATOR_SIZE)
            .background(
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                shape = CircleShape
            )
            .padding(2.dp)
            .background(
                color = color,
                shape = CircleShape
            )
    ) {  }
}

private val THEME_INDICATOR_SIZE: Dp = 32.dp
