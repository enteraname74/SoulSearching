package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
) {
    Checkbox(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand),
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkmarkColor = color,
            checkedColor = Color.Transparent,
            uncheckedColor = color,
        )
    )
}