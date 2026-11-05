package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

@Composable
internal actual fun provideDirectoryPickerSettings(): FileKitDialogSettings = FileKitDialogSettings.createDefault()