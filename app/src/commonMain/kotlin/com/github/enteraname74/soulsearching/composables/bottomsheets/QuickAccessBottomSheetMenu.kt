package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement

@Deprecated("Use new BottomSheetRowSpec system")
@Composable
fun QuickAccessBottomSheetMenu(
    isElementInQuickAccess: Boolean,
    quickAccessAction: () -> Unit,
    soulSearchingSettings: SoulSearchingSettings = injectElement(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isQuickAccessShown: Boolean = soulSearchingSettings.get(
        settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
    )

    Column {
        if (isQuickAccessShown) {
            BottomSheetRowUi(
                icon = Icons.Rounded.DoubleArrow,
                text = if (isElementInQuickAccess) {
                    strings.removeFromQuickAccess
                } else {
                    strings.addToQuickAccess
                },
                onClick = quickAccessAction,
            )
        }
        content()
    }
}