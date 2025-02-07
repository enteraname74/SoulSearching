package com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.card.SoulInformationCard
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun MultipleArtistsWarningCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(UiConstants.Spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        SoulInformationCard(
            modifier = Modifier
                .widthIn(
                    min = CardMinWidth,
                    max = CardMaxWidth
                ),
            title = null,
            text = strings.multipleArtistsText,
            icon = Icons.Rounded.Groups,
            buttonSpec = null,
        )
    }
}

private val CardMinWidth: Dp = 0.dp
private val CardMaxWidth: Dp = 500.dp
