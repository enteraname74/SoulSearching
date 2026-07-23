package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.domain.util.PlatformUtils
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_arrow_back
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_arrow_forward
import com.github.enteraname74.soulsearching.coreui.ext.isMouseAndKeyboardOnly
import com.github.enteraname74.soulsearching.coreui.navigation.SoulPageIndicator

@Composable
fun SettingsStatisticsSectionIndicatorList(
    selectedIndex: Int,
    listSize: Int,
    onClick: (selectedIndex: Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        if (PlatformUtils.isMouseAndKeyboardOnly()) {
            SoulIconButton(
                icon = CoreRes.drawable.ic_arrow_back,
                onClick = {
                    onClick(
                        if (selectedIndex == 0) {
                            listSize - 1
                        } else {
                            selectedIndex - 1
                        }
                    )
                },
                colors = SoulButtonDefaults.primaryColors(
                    containerColor = Color.Transparent,
                )
            )
        }

        (0 until listSize).forEach { index ->
            SoulPageIndicator(
                isSelected = index == selectedIndex,
                onClick = {
                    onClick(index)
                }
            )
        }

        if (PlatformUtils.isMouseAndKeyboardOnly()) {
            SoulIconButton(
                icon = CoreRes.drawable.ic_arrow_forward,
                onClick = {
                    onClick((selectedIndex + 1).mod(listSize))
                },
                colors = SoulButtonDefaults.primaryColors(
                    containerColor = Color.Transparent,
                )
            )
        }
    }
}
