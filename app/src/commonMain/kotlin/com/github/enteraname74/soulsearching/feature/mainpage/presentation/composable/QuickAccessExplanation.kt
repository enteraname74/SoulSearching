package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun QuickAccessExplanation() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = UiConstants.Spacing.large,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(
                    max = 700.dp,
                ),
            colors = CardDefaults.cardColors(
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                containerColor = SoulSearchingColorTheme.colorScheme.secondary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
}
