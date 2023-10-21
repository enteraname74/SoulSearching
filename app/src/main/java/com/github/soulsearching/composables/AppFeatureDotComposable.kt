package com.github.soulsearching.composables

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
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun AppFeatureDotComposable(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(Constants.ImageSize.medium)
            .padding(Constants.Spacing.small)
    ) {
        Image(
            modifier = Modifier
                .size(Constants.ImageSize.small)
                .align(Alignment.Center)
                .clickable {
                    onClick()
                },
            colorFilter = ColorFilter.tint(DynamicColor.onPrimary),
            imageVector = if (isSelected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
            contentDescription = ""
        )
    }

}