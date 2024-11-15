package com.github.enteraname74.soulsearching.composables.bottomsheets.multiartist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun MultiArtistBottomSheetMenu(
    total: Int,
    deleteAction: () -> Unit,
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .background(color = SoulSearchingColorTheme.colorScheme.secondary)
    ) {
        BottomSheetElementInformation(
            title = strings.multipleSelection,
            subTitle = strings.selectedElements(total = total),
            cover = null,
        )

        BottomSheetRow(
            icon = Icons.Rounded.Delete,
            text = strings.deleteSelectedArtists,
            onClick = deleteAction
        )
    }
}