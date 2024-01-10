package com.github.soulsearching.composables.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.theme.SoulSearchingColorTheme

@Composable
fun SettingsElement(
    title: String,
    text: String,
    icon: ImageVector? = null,
    clickAction: () -> Unit = {},
    padding: PaddingValues = PaddingValues(Constants.Spacing.veryLarge)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                clickAction()
            }
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.large)
    ) {
        icon?.let {
            Image(
                modifier = Modifier.size(Constants.ImageSize.medium),
                imageVector = it,
                contentDescription = "",
                colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onPrimary)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = text,
                color = SoulSearchingColorTheme.colorScheme.subText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}