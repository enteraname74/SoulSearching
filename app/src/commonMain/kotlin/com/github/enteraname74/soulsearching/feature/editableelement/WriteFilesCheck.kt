package com.github.enteraname74.soulsearching.feature.editableelement

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
expect fun WriteFilesCheck(
    onSave: () -> Unit,
    musicsToSave: List<Music>,
    conditionToAskWriteFiles: () -> Boolean = { true },
    settingsRepository: SettingsRepository = injectElement(),
    content: @Composable (onSave: () -> Unit) -> Unit,
)
