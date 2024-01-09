package com.github.soulsearching.composables.bottomsheets.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.soulsearching.strings
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.utils.SettingsUtils

@Composable
fun AlbumBottomSheetMenu(
    modifyAction: () -> Unit,
    deleteAction: () -> Unit,
    quickAccessAction: () -> Unit,
    isInQuickAccess: Boolean
) {
    Column(
        modifier = Modifier
            .background(color = DynamicColor.secondary)
            .padding(Constants.Spacing.large)
    ) {
        if (SettingsUtils.settingsViewModel.handler.isQuickAccessShown) {
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