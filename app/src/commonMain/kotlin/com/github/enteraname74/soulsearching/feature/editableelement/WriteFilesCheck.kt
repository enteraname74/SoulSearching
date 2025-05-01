package com.github.enteraname74.soulsearching.feature.editableelement

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
expect fun WriteFilesCheck(
    onSave: () -> Unit,
    musicsToSave: List<Music>,
    settings: SoulSearchingSettings = injectElement(),
    content: @Composable (onSave: () -> Unit) -> Unit,
)