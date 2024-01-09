package com.github.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.strings
import com.github.soulsearching.theme.DynamicColor

@Composable
fun SoulSearchingLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.ImageSize.veryLarge),
            painter = SoulSearchingContext.defaultLogoPainter,
            contentDescription = strings.appLogo,
            colorFilter = ColorFilter.tint(color = DynamicColor.onPrimary)
        )
        Text(
            text = strings.appName,
            color = DynamicColor.onPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}