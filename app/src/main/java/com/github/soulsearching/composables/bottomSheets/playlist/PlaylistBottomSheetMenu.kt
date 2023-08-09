package com.github.soulsearching.composables.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R

@Composable
fun PlaylistBottomSheetMenu(
    isFavoritePlaylist : Boolean,
    modifyAction : () -> Unit,
    deleteAction : () -> Unit,
    addToShortcutsAction : () -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(Constants.Spacing.large)
    ) {
        BottomSheetRow(
            icon = Icons.Default.DoubleArrow,
            text = stringResource(id = R.string.add_to_shortcuts),
            onClick = addToShortcutsAction
        )
        BottomSheetRow(
            icon = Icons.Default.Edit,
            text = stringResource(id = R.string.modify_playlist),
            onClick = modifyAction
        )
        if (!isFavoritePlaylist) {
            BottomSheetRow(
                icon = Icons.Default.Delete,
                text = stringResource(id = R.string.delete_playlist),
                onClick = deleteAction
            )
        }
    }
}