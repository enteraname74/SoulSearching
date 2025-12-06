package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun QuickAccessExplanation() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .padding(UiConstants.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(
                UiConstants.Spacing.medium,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = strings.emptyQuickAccess,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                style = UiConstants.Typography.bodyTitle,
            )
            Text(
                text = strings.quickAccessExplanation,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                style = UiConstants.Typography.body,
            )
        }
    }
}