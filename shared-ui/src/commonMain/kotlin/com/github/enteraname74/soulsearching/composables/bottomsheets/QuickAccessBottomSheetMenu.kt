package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement

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

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .background(color = SoulSearchingColorTheme.colorScheme.secondary)
    ) {
        if (isQuickAccessShown) {
            BottomSheetRow(
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