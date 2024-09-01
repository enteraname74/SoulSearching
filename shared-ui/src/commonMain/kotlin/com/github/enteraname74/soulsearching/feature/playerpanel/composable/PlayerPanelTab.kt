package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomSheetState
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
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager

@Composable
fun PlayerPanelTab(
    modifier: Modifier = Modifier,
    title: String,
    contentColor: Color,
    isSelected: Boolean,
    onSelected: () -> Unit,
    playerViewManager: PlayerViewManager = injectElement(),
) {
    Column(
        modifier = Modifier
            .height(
                IntrinsicSize.Min
            ).then(
                modifier
            ).clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                onSelected()
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