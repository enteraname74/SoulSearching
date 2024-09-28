package com.github.enteraname74.soulsearching.feature.settings.colortheme.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsActionElement

@Composable
fun ColorCard(
    title: String,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    subComposable: @Composable (() -> Unit)
) {
    Card(
        modifier = Modifier
            .alpha(if (isSelected) 1.0f else ContentAlpha.disabled),
        colors = CardDefaults.cardColors(
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .clickableWithHandCursor { onClick() }
                .padding(
                    all = UiConstants.Spacing.large,
                )
        ) {
            SettingsActionElement(
                clickEnabled = false,
                title = title,
                subTitle = text,
                clickAction = onClick,
                isSelected = isSelected,
                padding = 0.dp,
                textColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                subTextColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
            )
            subComposable()
        }
    }
}