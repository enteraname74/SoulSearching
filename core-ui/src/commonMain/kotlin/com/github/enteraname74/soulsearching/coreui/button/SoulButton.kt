package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = SoulButtonDefaults.shape,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
    contentPadding: PaddingValues = SoulButtonDefaults.contentPadding(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false,
    ) {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.containerColor,
                contentColor = colors.contentColor,
            ),
            contentPadding = contentPadding,
        ) {
            content()
        }
    }

}