package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.soulsearching.coreui.strings.strings

interface TopBarActionSpec {
    val icon: ImageVector
    val onClick: () -> Unit
    val isEnabled: Boolean
        get() = true

    val contentDescription: String?
        @Composable
        get() = null
}

data class TopBarNavigationAction(
    override val onClick: () -> Unit,
) : TopBarActionSpec {
    override val icon: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack
    override val isEnabled: Boolean = true
    override val contentDescription: String
        @Composable
        get() = strings.backButton
}

data class TopBarValidateAction(
    override val onClick: () -> Unit,
    override val isEnabled: Boolean = true,
) : TopBarActionSpec {
    override val icon: ImageVector = Icons.Rounded.Done
}