package com.github.enteraname74.soulsearching.feature.settings.cloud.users

import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlin.uuid.Uuid

data class SettingsCloudUsersState(
    val users: List<UiSimpleUser>,
    val dialog: SoulDialog?,
)

data class UiSimpleUser(
    val id: Uuid,
    val name: String,
    val onDelete: (() -> Unit)?
)
