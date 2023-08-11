package com.github.soulsearching.composables.searchComposables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SearchType(
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DynamicColor.primary)
            .padding(
                top = Constants.Spacing.medium,
                bottom = Constants.Spacing.medium
            )
    ) {
        Text(
            text = title,
            color = DynamicColor.onPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}