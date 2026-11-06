package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.back_button
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_arrow_back
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_check
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ok
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import org.jetbrains.compose.resources.DrawableResource

interface SoulTopBarAction {
    val content: @Composable () -> Unit

    data class IconButton(
        val icon: DrawableResource,
        val contentDescription: SoulTextSpec?,
        val colors: SoulButtonColors,
        val enabled: Boolean,
        val onClick: () -> Unit,
    ) : SoulTopBarAction {
        override val content: @Composable (() -> Unit) = {
            SoulIconButton(
                icon = icon,
                contentDescription = contentDescription?.string,
                colors = colors.copy(
                    contentColor = if (enabled) {
                        colors.contentColor
                    } else {
                        colors.contentColor.copy(UiConstants.ALPHA_DISABLED)
                    }
                ),
                onClick = onClick,
                enabled = enabled,
            )
        }
    }

    companion object {
        @Composable
        fun navigation(
            onClick: () -> Unit,
            enabled: Boolean = true,
            colors: SoulButtonColors = SoulButtonDefaults.primaryTint(),
        ): IconButton =
            IconButton(
                icon = CoreRes.drawable.ic_arrow_back,
                contentDescription = CoreRes.string.back_button.toTextSpec(),
                colors = colors,
                enabled = enabled,
                onClick = onClick,
            )

        @Composable
        fun validate(
            onClick: () -> Unit,
            enabled: Boolean = true,
            colors: SoulButtonColors = SoulButtonDefaults.primaryTint(),
        ): IconButton =
            IconButton(
                icon = CoreRes.drawable.ic_check,
                contentDescription = CoreRes.string.ok.toTextSpec(),
                colors = colors,
                enabled = enabled,
                onClick = onClick,
            )

        @Composable
        fun icon(
            icon: DrawableResource,
            onClick: () -> Unit,
            enabled: Boolean = true,
            contentDescription: SoulTextSpec? = null,
            colors: SoulButtonColors = SoulButtonDefaults.primaryTint(),
        ): IconButton =
            IconButton(
                icon = icon,
                contentDescription = contentDescription,
                colors = colors,
                enabled = enabled,
                onClick = onClick,
            )
    }
}