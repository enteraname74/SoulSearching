package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
fun AlbumBottomSheetMenu(
    modifyAction: () -> Unit,
    deleteAction: () -> Unit,
    quickAccessAction: () -> Unit,
    isInQuickAccess: Boolean,
    soulSearchingSettings: SoulSearchingSettings = injectElement(),
) {

    val isQuickAccessShown: Boolean = soulSearchingSettings.get(
        settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
    )

    Column(
        modifier = Modifier
            .background(color = SoulSearchingColorTheme.colorScheme.secondary)
            .navigationBarsPadding()
            .padding(UiConstants.Spacing.large)
    ) {
        if (isQuickAccessShown) {
            BottomSheetRow(
                icon = Icons.Rounded.DoubleArrow,
                text = if (isInQuickAccess) {
                    strings.removeFromQuickAccess
                } else {
                    strings.addToQuickAccess
                },
                onClick = quickAccessAction
            )
        }
        BottomSheetRow(
            icon = Icons.Rounded.Edit,
            text = strings.modifyAlbum,
            onClick = modifyAction
        )
        BottomSheetRow(
            icon = Icons.Rounded.Delete,
            text = strings.deleteAlbum,
            onClick = deleteAction
        )
    }
}