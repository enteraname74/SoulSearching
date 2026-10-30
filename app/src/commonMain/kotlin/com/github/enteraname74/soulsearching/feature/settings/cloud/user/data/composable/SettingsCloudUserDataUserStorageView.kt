package com.github.enteraname74.soulsearching.feature.settings.cloud.user.data.composable

import org.jetbrains.compose.resources.stringResource
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
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.domain.model.user.UserStorage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_info_outlined
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlinx.coroutines.launch
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

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
        SoulText(
            text = CoreRes.string.user_storage_section_title.toTextSpec(),
            style = UiConstants.Typography.bodyTitle,
            color = SoulSearchingColorTheme.colorScheme.text,
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
        SoulText(
            modifier = Modifier
                .offset(y = 4.dp),
            text = "${userStorage.current}".toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.titleBig,
        )
        SoulText(
            text = " ${stringResource(CoreRes.string.gigabyte_abbreviation)}".toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.bodyLarge,
        )
        SoulText(
            modifier = Modifier
                .padding(horizontal = UiConstants.Spacing.medium),
            text = "/".toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.body,
        )
        SoulText(
            text = "${userStorage.max.total} ${stringResource(CoreRes.string.gigabyte_abbreviation)}".toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.text,
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
            positioning = TooltipAnchorPosition.Below,
            spacingBetweenTooltipAndAnchor = 0.dp,
        ),
        state = tooltipState,
        tooltip = {
            RichTooltip(
                colors = TooltipDefaults.richTooltipColors(
                    containerColor = SoulSearchingColorTheme.colorScheme.surface,
                ),
                caretShape = TooltipDefaults.caretShape(),
            ) {
                SoulText(
                    text = when (storageType) {
                        is UserStorage.StorageType.AllAvailable -> CoreRes.string.user_storage_all_available_info.toTextSpec()
                        is UserStorage.StorageType.Reduced -> CoreRes.string.user_storage_reduced_info.toTextSpec()
                    },
                    color = SoulSearchingColorTheme.colorScheme.text,
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
        val usedSpaceBarWidth = (maxWidth * userStorage.current.toFloat()) / userStorage.max.total.toFloat()
        StorageBar(
            width = usedSpaceBarWidth,
            color = SoulSearchingColorTheme.colorScheme.text,
        )
    }
}

@Composable
private fun StorageBar(
    width: Dp,
    color: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(shape = RoundedCornerShape(percent = 50))
            .background(
                color = SoulSearchingColorTheme.colorScheme.surface,
            )
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(16.dp)
                .background(color = color)
        )
    }
}