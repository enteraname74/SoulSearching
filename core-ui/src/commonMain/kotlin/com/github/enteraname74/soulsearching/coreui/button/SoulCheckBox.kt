package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkmarkColor = color,
            checkedColor = Color.Transparent,
            uncheckedColor = color,
        )
    )
}