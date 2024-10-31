package com.github.enteraname74.soulsearching.feature.appinit.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppFeatureComposable(
    title: String,
    description: String,
    image: DrawableResource,
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            contentColor = SoulSearchingColorTheme.colorScheme.secondary,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(UiConstants.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            Text(
                text = title,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .weight(1F),
                    painter = painterResource(image),
                    contentDescription = ""
                )
            }
        }
    }
}