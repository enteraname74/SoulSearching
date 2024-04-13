package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.strings.strings

@Composable
fun AppHeaderBar(
    modifier: Modifier = Modifier,
    title: String,
    leftAction: () -> Unit,
    leftIcon: ImageVector = Icons.Rounded.ArrowBack,
    rightAction: () -> Unit = {},
    rightIcon: ImageVector? = null,
    backgroundColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    Row(
        modifier = modifier
            .then(
                Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = leftAction) {
            Icon(
                imageVector = leftIcon,
                contentDescription = strings.backButton,
                tint = contentColor
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                maxLines = 2,
                fontSize = 18.sp,
                color = contentColor,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        if (rightIcon != null) {
            IconButton(onClick = rightAction) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = strings.headerBarRightButton,
                    tint = contentColor
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}
