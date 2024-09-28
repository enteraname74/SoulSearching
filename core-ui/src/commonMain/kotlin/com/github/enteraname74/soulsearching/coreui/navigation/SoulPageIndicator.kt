package com.github.enteraname74.soulsearching.coreui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulPageIndicator(
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Spacer(
        modifier = Modifier
            .size(INDICATOR_SIZE)
            .clip(CircleShape)
            .clickableWithHandCursor { onClick() }
            .background(
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                shape = CircleShape,
            )
            .padding(all = RING_SIZE)
            .background(
                color = if (isSelected) {
                    SoulSearchingColorTheme.colorScheme.onPrimary
                } else {
                    SoulSearchingColorTheme.colorScheme.primary
                },
                shape = CircleShape,
            )
    )
}

private val INDICATOR_SIZE: Dp = 16.dp
private val RING_SIZE: Dp = 2.dp