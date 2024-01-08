package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.R
import com.github.soulsearching.theme.DynamicColor

@Composable
fun AppHeaderBar(
    title : String,
    leftAction : () -> Unit,
    leftIcon : ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    rightAction : () -> Unit = {},
    rightIcon : ImageVector? = null,
    backgroundColor: Color = DynamicColor.primary,
    contentColor: Color = DynamicColor.onPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = leftAction) {
            Icon(
                imageVector = leftIcon,
                contentDescription = stringResource(id = R.string.back_button),
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
                    contentDescription = stringResource(id = R.string.header_bar_right_button),
                    tint = contentColor
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}
