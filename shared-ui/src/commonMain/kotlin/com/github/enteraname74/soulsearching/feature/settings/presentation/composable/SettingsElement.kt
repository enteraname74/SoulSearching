package com.github.enteraname74.soulsearching.feature.settings.presentation.composable

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
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SettingsElement(
    title: String,
    text: String,
    icon: ImageVector? = null,
    isClickable: Boolean = true,
    clickAction: () -> Unit = {},
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.veryLarge)
) {

    val clickableModifier = if (isClickable) Modifier.clickable { clickAction() } else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
    ) {
        icon?.let {
            Image(
                modifier = Modifier.size(UiConstants.ImageSize.medium),
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
                color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}