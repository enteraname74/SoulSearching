package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import com.github.enteraname74.soulsearching.coreui.strings.strings
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

internal actual fun provideDirectoryPickerSettings(): FileKitDialogSettings = FileKitDialogSettings(
    title = strings.coverFolderRetrieverPathSelectionTitle,
)