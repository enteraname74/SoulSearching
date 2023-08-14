package com.github.soulsearching.composables.bottomSheets.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.composables.bottomSheets.BottomSheetRow
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun ArtistBottomSheetMenu(
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
        BottomSheetRow(
            icon = Icons.Default.DoubleArrow,
            text = if (isInQuickAccess) {
                stringResource(id = R.string.remove_from_quick_access)
            } else {
                stringResource(id = R.string.add_to_quick_access)
            },
            onClick = quickAccessAction
        )
        BottomSheetRow(
            icon = Icons.Default.Edit,
            text = stringResource(id = R.string.modify_artist),
            onClick = modifyAction
        )
        BottomSheetRow(
            icon = Icons.Default.Delete,
            text = stringResource(id = R.string.delete_artist),
            onClick = deleteAction
        )
    }
}
