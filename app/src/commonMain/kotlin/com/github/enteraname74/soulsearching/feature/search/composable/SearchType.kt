package com.github.enteraname74.soulsearching.feature.search.composable

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SearchType(
    title: SoulTextSpec,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(primaryColor)
            .padding(
                top = UiConstants.Spacing.medium,
                bottom = UiConstants.Spacing.medium
            )
    ) {
        SoulText(
            text = title,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
