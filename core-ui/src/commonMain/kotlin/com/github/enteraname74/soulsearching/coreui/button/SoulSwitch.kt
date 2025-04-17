package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulSwitch(
    isChecked: Boolean,
    onClick: () -> Unit,
    colors: SoulSwitchColors = SoulSwitchDefaults.colors()
) {
    Switch(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand),
        checked = isChecked,
        onCheckedChange = { onClick() },
        colors = SwitchDefaults.colors(
            checkedThumbColor = colors.outer,
            checkedTrackColor = colors.inner,
            checkedBorderColor = colors.inner,
            uncheckedThumbColor = colors.outer,
            uncheckedTrackColor = Color.Transparent,
            uncheckedBorderColor = colors.outer,
        )
    )
}

data class SoulSwitchColors(
    val outer: Color,
    val inner: Color,
)

object SoulSwitchDefaults {
    @Composable
    fun colors(
        outer: Color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
        inner: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    ): SoulSwitchColors = SoulSwitchColors(
        outer = outer,
        inner = inner,
    )
}