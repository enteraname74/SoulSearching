package com.github.enteraname74.soulsearching.composables.navigation

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings_filled
import com.github.enteraname74.soulsearching.ext.filledIcon
import com.github.enteraname74.soulsearching.ext.outlinedIcon
import com.github.enteraname74.soulsearching.ext.text
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import org.jetbrains.compose.resources.DrawableResource

sealed interface NavigationRowSpec {
    val title: SoulTextSpec
    val filledIcon: DrawableResource
    val outlinedIcon: DrawableResource
    val trailingText: SoulTextSpec?
    val onClick: () -> Unit

    fun icon(isSelected: Boolean): DrawableResource =
        if (isSelected) {
            filledIcon
        } else {
            outlinedIcon
        }

    data class Settings(
        val isBadged: Boolean,
        override val onClick: () -> Unit,
    ) : NavigationRowSpec {
        override val trailingText: SoulTextSpec? = null
        override val title: SoulTextSpec = CoreRes.string.settings.toTextSpec()
        override val filledIcon: DrawableResource = CoreRes.drawable.ic_settings_filled
        override val outlinedIcon: DrawableResource = CoreRes.drawable.ic_settings
    }

    data class Search(
        override val onClick: () -> Unit,
    ) : NavigationRowSpec {
        override val title: SoulTextSpec = CoreRes.string.search.toTextSpec()
        override val filledIcon: DrawableResource = CoreRes.drawable.ic_search
        override val outlinedIcon: DrawableResource = CoreRes.drawable.ic_search
        override val trailingText: SoulTextSpec? = null
    }

    data class MainTabElement(
        private val element: ElementEnum,
        override val trailingText: SoulTextSpec?,
        val isCurrentPage: Boolean,
        override val onClick: () -> Unit,
    ) : NavigationRowSpec {
        override val title: SoulTextSpec = element.text()
        override val filledIcon: DrawableResource = element.filledIcon()
        override val outlinedIcon: DrawableResource = element.outlinedIcon()
    }
}
