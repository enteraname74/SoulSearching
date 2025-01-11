package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SoulSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colors: SoulSwitchColors = SoulSwitchDefaults.secondaryColors(),
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = colors.thumbColor,
            checkedTrackColor = colors.containerColor,
            checkedBorderColor = colors.containerColor,
            uncheckedThumbColor = colors.thumbColor,
            uncheckedTrackColor = Color.Transparent,
            uncheckedBorderColor = colors.thumbColor,
        )
    )
}