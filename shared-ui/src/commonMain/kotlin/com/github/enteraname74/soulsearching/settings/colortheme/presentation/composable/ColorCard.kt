package com.github.soulsearching.settings.colortheme.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun ColorCard(
    title: String,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = UiConstants.Spacing.large,
    subComposable: @Composable (() -> Unit)
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(
                top = UiConstants.Spacing.large,
                bottom = UiConstants.Spacing.large,
                start = UiConstants.Spacing.veryLarge,
                end = UiConstants.Spacing.veryLarge,
            )
            .alpha(if (isSelected) 1.0f else ContentAlpha.disabled),
        colors = CardDefaults.cardColors(
            contentColor = SoulSearchingColorTheme.colorScheme.secondary,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            SettingsActionElement(
                title = title,
                text = text,
                clickAction = onClick,
                isSelected = isSelected,
                padding = 0.dp
            )
            subComposable()
        }
    }
}