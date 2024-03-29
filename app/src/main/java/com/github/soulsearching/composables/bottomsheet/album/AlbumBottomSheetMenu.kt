package com.github.soulsearching.composables.bottomsheet.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.utils.SettingsUtils
import com.github.soulsearching.composables.bottomsheet.BottomSheetRow
import com.github.soulsearching.ui.theme.DynamicColor

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
        if (SettingsUtils.settingsViewModel.isQuickAccessShown) {
            BottomSheetRow(
                icon = Icons.Rounded.DoubleArrow,
                text = if (isInQuickAccess) {
                    stringResource(id = R.string.remove_from_quick_access)
                } else {
                    stringResource(id = R.string.add_to_quick_access)
                },
                onClick = quickAccessAction
            )
        }
        BottomSheetRow(
            icon = Icons.Rounded.Edit,
            text = stringResource(id = R.string.modify_album),
            onClick = modifyAction
        )
        BottomSheetRow(
            icon = Icons.Rounded.Delete,
            text = stringResource(id = R.string.delete_album),
            onClick = deleteAction
        )
    }
}