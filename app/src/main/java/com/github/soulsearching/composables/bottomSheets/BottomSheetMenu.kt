package com.github.soulsearching.composables.bottomSheets

import android.util.Log
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
fun BottomSheetMenu(
    modifyAction: () -> Unit,
    removeAction: () -> Unit,
    addToShortcutsAction: () -> Unit = {},
    addToPlaylistAction: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(Constants.Spacing.large)
    ) {
        Log.d("DIFFERENT", (addToShortcutsAction != {}).toString())
        if (addToShortcutsAction != {}) {
            BottomSheetRow(
                icon = Icons.Default.DoubleArrow,
                text = stringResource(id = R.string.add_to_shortcuts),
                onClick = {})
        }
        if (addToPlaylistAction != {}) BottomSheetRow(
            icon = Icons.Default.PlaylistAdd,
            text = stringResource(id = R.string.add_to_playlist),
            onClick = addToPlaylistAction
        )
        BottomSheetRow(
            icon = Icons.Default.Delete,
            text = stringResource(id = R.string.delete_music),
            onClick = removeAction
        )
        BottomSheetRow(
            icon = Icons.Default.Edit,
            text = stringResource(id = R.string.modify_music),
            onClick = modifyAction
        )
        BottomSheetRow(
            icon = Icons.Default.PlaylistPlay,
            text = stringResource(id = R.string.play_next),
            onClick = {})
    }
}