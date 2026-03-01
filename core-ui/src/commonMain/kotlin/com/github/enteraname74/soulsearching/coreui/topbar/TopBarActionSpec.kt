package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_arrow_back
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_check
import com.github.enteraname74.soulsearching.coreui.strings.strings
import org.jetbrains.compose.resources.DrawableResource

interface TopBarActionSpec {
    val icon: DrawableResource
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
    override val icon: DrawableResource = CoreRes.drawable.ic_arrow_back
    override val isEnabled: Boolean = true
    override val contentDescription: String
        @Composable
        get() = strings.backButton
}

data class TopBarValidateAction(
    override val onClick: () -> Unit,
    override val isEnabled: Boolean = true,
) : TopBarActionSpec {
    override val icon: DrawableResource = CoreRes.drawable.ic_check
}