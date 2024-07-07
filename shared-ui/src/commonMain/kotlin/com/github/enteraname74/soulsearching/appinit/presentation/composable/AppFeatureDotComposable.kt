package com.github.soulsearching.appinit.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun AppFeatureDotComposable(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(UiConstants.ImageSize.medium)
            .padding(UiConstants.Spacing.small)
    ) {
        Image(
            modifier = Modifier
                .size(UiConstants.ImageSize.small)
                .align(Alignment.Center)
                .clickable {
                    onClick()
                },
            colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onPrimary),
            imageVector = if (isSelected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
            contentDescription = ""
        )
    }
}