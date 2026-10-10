package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

@Composable
fun EditableElementCoverSection(
    title: String,
    coverEditMode: CoverEditMode,
    onSelectImage: (pos: Int) -> Unit,
    onSwitchModeType: (new: CoverEditMode.Type) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = UiConstants.Spacing.medium),
            text = title,
            color = SoulSearchingColorTheme.colorScheme.onPrimary
        )
        CoverEditModeImage(
            coverEditMode = coverEditMode,
            onSelectImage = onSelectImage,
            onSwitchModeType = onSwitchModeType,
        )
    }
}
