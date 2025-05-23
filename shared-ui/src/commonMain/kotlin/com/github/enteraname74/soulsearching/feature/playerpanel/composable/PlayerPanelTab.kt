package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
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
                .animateContentSize(animationSpec = tween(UiConstants.AnimationDuration.normal))
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val weight: Int by animateIntAsState(
                targetValue = if (isSelected) FontWeight.Bold.weight else FontWeight.Normal.weight,
                animationSpec = tween(UiConstants.AnimationDuration.normal)
            )

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight(weight),
                color = contentColor,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        val dividerColor: Color by animateColorAsState(
            targetValue = if (isSelected) contentColor else Color.Transparent,
            animationSpec = tween(UiConstants.AnimationDuration.normal)
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(
                    horizontal = UiConstants.Spacing.mediumPlus
                ),
            thickness = 2.dp,
            color = dividerColor
        )
    }
}