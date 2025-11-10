package com.github.enteraname74.soulsearching.coreui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulDivider(
    color: Color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
    padding: PaddingValues = PaddingValues(vertical = UiConstants.Spacing.large),
) {
    HorizontalDivider(
        modifier = Modifier
            .padding(padding),
        thickness = 1.dp,
        color = color,
    )
}