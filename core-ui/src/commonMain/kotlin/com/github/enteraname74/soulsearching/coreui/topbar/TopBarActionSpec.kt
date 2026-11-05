package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.back_button
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_arrow_back
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_check
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

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
        get() = stringResource(CoreRes.string.back_button)
}

data class TopBarValidateAction(
    override val onClick: () -> Unit,
    override val isEnabled: Boolean = true,
) : TopBarActionSpec {
    override val icon: DrawableResource = CoreRes.drawable.ic_check
}
