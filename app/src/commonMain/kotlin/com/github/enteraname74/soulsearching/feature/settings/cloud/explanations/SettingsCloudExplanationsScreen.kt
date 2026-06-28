package com.github.enteraname74.soulsearching.feature.settings.cloud.explanations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_open_in_new
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SettingsCloudExplanationsScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus)
    ) {
        Text(
            text = strings.cloudExplanationsTitle,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleSmall,
        )
        Text(
            text = strings.cloudExplanationsText,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.body,
        )
        AlphaWarningCard()
    }
}

@Composable
private fun AlphaWarningCard() {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .clickableWithHandCursor {
                    uriHandler.openUri(strings.cloudDocumentationURL)
                }
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                containerColor = SoulSearchingColorTheme.colorScheme.primary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UiConstants.Spacing.large),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = UiConstants.Spacing.large,
                )
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = strings.cloudExplanationsRedirect,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    style = UiConstants.Typography.body,
                )
                SoulIcon(
                    icon = CoreRes.drawable.ic_open_in_new,
                    size = UiConstants.ImageSize.medium,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}