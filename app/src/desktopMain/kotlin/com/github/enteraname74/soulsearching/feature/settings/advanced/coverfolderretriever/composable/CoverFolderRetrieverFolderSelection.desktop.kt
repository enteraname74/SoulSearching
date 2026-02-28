package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import com.github.enteraname74.soulsearching.coreui.strings.strings
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

actual fun buildDialogSettings(): FileKitDialogSettings =
    FileKitDialogSettings(
        title = strings.coverFolderRetrieverPathSelectionTitle,
    )