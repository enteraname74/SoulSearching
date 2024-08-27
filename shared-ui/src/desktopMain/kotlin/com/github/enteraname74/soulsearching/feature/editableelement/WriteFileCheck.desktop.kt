package com.github.enteraname74.soulsearching.feature.editableelement

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings

@Composable
actual fun WriteFilesCheck(
    onSave: () -> Unit,
    musicsToSave: List<Music>,
    settings: SoulSearchingSettings,
    content: @Composable (onSave: () -> Unit) -> Unit,
) {
    content(onSave)
}