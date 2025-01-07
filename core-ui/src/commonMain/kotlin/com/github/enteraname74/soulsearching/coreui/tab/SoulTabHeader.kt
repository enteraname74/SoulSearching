package com.github.enteraname74.soulsearching.coreui.tab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf

@Composable
fun SoulTabHeader(
    title: String,
    contentColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isClickable: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .then(modifier)
            .clickableIf(enabled = isClickable) {
                onClick()
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        if (isSelected) {
            HorizontalDivider(
                thickness = 2.dp,
                color = contentColor
            )
        }
    }
}