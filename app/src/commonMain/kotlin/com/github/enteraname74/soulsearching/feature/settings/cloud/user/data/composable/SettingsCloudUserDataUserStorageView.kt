package com.github.enteraname74.soulsearching.feature.settings.cloud.user.data.composable

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.user.UserStorage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_info_outlined
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlinx.coroutines.launch

@Composable
internal fun SettingsCloudUserDataUserStorageView(
    userStorage: UserStorage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        SectionTitle(type = userStorage.max)
        Total(userStorage)
        Bars(userStorage)
    }
}

@Composable
private fun SectionTitle(
    type: UserStorage.StorageType
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = strings.userStorageSectionTitle,
            style = UiConstants.Typography.bodyTitle,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
        MoreInfoButton(
            storageType = type,
        )
    }
}

@Composable
private fun Total(
    userStorage: UserStorage,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            modifier = Modifier
                .offset(y = 4.dp),
            text = "${userStorage.current}",
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleBig,
        )
        Text(
            text = " ${strings.gigabyteAbbreviation}",
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.bodyLarge,
        )
        Text(
            modifier = Modifier
                .padding(horizontal = UiConstants.Spacing.medium),
            text = "/",
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.body,
        )
        Text(
            text = "${userStorage.max.total} ${strings.gigabyteAbbreviation}",
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.bodyLarge,
        )
    }
}

@Composable
private fun MoreInfoButton(
    storageType: UserStorage.StorageType,
) {
    val tooltipState = rememberTooltipState()
    val coroutineScope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above,
            spacingBetweenTooltipAndAnchor = 0.dp,
        ),
        state = tooltipState,
        tooltip = {
            RichTooltip(
                colors = TooltipDefaults.richTooltipColors(
                    containerColor = SoulSearchingColorTheme.colorScheme.secondary,
                ),
                caretShape = TooltipDefaults.caretShape(),
            ) {
                Text(
                    text = when (storageType) {
                        is UserStorage.StorageType.AllAvailable -> strings.userStorageAllAvailableInfo
                        is UserStorage.StorageType.Reduced -> strings.userStorageReducedInfo
                    },
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                    style = UiConstants.Typography.bodySmall,
                )
            }
        }
    ) {
        SoulIconButton(
            icon = CoreRes.drawable.ic_info_outlined,
            colors = SoulButtonDefaults.primaryTint(),
            onClick = {
                coroutineScope.launch {
                    if (tooltipState.isVisible) {
                        tooltipState.dismiss()
                    } else {
                        tooltipState.show(MutatePriority.UserInput)
                    }
                }
            }
        )
    }
}

@Composable
private fun Bars(
    userStorage: UserStorage,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ProgressBar(
            width = maxWidth,
            color = SoulSearchingColorTheme.colorScheme.secondary,
        )
        val usedSpaceBarWidth = (maxWidth * userStorage.current.toFloat()) / userStorage.max.total.toFloat()
        ProgressBar(
            width = usedSpaceBarWidth,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
        )
    }
}

@Composable
private fun ProgressBar(
    width: Dp,
    color: Color,
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(16.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(percent = 50),
            )
    )
}