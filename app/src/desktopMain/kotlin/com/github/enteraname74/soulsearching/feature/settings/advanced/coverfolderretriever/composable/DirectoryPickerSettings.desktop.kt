package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun provideDirectoryPickerSettings(): FileKitDialogSettings = FileKitDialogSettings(
    title = stringResource(CoreRes.string.cover_folder_retriever_path_selection_title),
)