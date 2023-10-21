package com.github.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun AppFeatureComposable(
    title: String,
    description: String,
    imageId: Int
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            contentColor = DynamicColor.secondary,
            containerColor = DynamicColor.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Constants.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
        ) {
            Text(
                text = title,
                color = DynamicColor.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                color = DynamicColor.subText,
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
                    painter = painterResource(imageId),
                    contentDescription = ""
                )
            }
        }
    }
}