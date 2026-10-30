package com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_groups_filled
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText

@Composable
fun MultipleArtistsWarningCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(UiConstants.Spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(
                    min = CardMinWidth,
                    max = CardMaxWidth
                ),
            colors = CardDefaults.cardColors(
                contentColor = SoulSearchingColorTheme.colorScheme.text,
                containerColor = SoulSearchingColorTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UiConstants.Spacing.large),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SoulIcon(
                    icon = CoreRes.drawable.ic_groups_filled,
                    size = UiConstants.ImageSize.mediumPlus,
                    color = SoulSearchingColorTheme.colorScheme.text,
                )
                SoulText(
                    text = CoreRes.string.multiple_artists_text.toTextSpec(),
                    color = SoulSearchingColorTheme.colorScheme.text,
                    textAlign = TextAlign.Center,
                    style = UiConstants.Typography.body,
                )
            }
        }
    }
}

private val CardMinWidth: Dp = 0.dp
private val CardMaxWidth: Dp = 500.dp
