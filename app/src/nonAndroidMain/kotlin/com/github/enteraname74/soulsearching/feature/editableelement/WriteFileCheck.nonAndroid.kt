package com.github.enteraname74.soulsearching.feature.editableelement

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository

@Composable
actual fun WriteFilesCheck(
    onSave: () -> Unit,
    musicsToSave: List<Music>,
    conditionToAskWriteFiles: () -> Boolean,
    settingsRepository: SettingsRepository,
    content: @Composable (onSave: () -> Unit) -> Unit,
) {
    content(onSave)
}
