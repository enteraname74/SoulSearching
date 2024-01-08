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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.theme.DynamicColor

@Composable
fun SoulSearchingLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.ImageSize.veryLarge),
            painter = painterResource(id = R.drawable.ic_saxophone_svg),
            contentDescription = stringResource(id = R.string.app_logo),
            colorFilter = ColorFilter.tint(color = DynamicColor.onPrimary)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            color = DynamicColor.onPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}