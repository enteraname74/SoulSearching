package com.github.enteraname74.soulsearching.feature.settings.cloud.user.data

import com.github.enteraname74.domain.model.user.UserStorage
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog

data class SettingsCloudUserDataState(
    val userStorage: UserStorage?,
    val dialogState: SoulDialog?,
    val onClearData: () -> Unit,
    val onNavigateBack: () -> Unit,
)