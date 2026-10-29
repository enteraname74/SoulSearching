package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlin.uuid.Uuid

data class SettingsCloudSharedListState(
    val previews: List<UiListPreview>,
    val dialog: SoulDialog?,
)

data class UiListPreview(
    val id: Uuid,
    val date: SoulTextSpec,
    val allUsers: SoulTextSpec,
    val connectedUsers: SoulTextSpec,
    val onDelete: (() -> Unit)?,
    val onJoin: () -> Unit,
)
