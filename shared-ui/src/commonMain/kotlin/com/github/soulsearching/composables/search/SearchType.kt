package com.github.soulsearching.composables.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.theme.DynamicColor

@Composable
fun SearchType(
    title: String,
    primaryColor: Color = DynamicColor.primary,
    textColor: Color = DynamicColor.onPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor)
            .padding(
                top = Constants.Spacing.medium,
                bottom = Constants.Spacing.medium
            )
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}