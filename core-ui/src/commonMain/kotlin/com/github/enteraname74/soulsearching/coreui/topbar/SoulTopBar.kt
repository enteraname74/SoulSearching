package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    leftAction: () -> Unit,
    leftIcon: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    rightAction: (() -> Unit)? = null,
    rightIcon: ImageVector? = null,
    containerColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    Row(
        modifier = modifier
            .then(
                Modifier
                    .fillMaxWidth()
                    .background(containerColor)
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
        title?.let {
            Text(
                modifier = Modifier.weight(1f),
                text = it,
                maxLines = 2,
                fontSize = 18.sp,
                color = contentColor,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        if (rightIcon != null && rightAction != null) {
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
