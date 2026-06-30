package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist

import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlin.uuid.Uuid

data class SettingsCloudSharedListState(
    val previews: List<UiListPreview>,
    val dialog: SoulDialog?,
)

data class UiListPreview(
    val id: Uuid,
    val date: String,
    val allUsers: String,
    val connectedUsers: String,
    val onDelete: (() -> Unit)?,
    val onJoin: () -> Unit,
)
