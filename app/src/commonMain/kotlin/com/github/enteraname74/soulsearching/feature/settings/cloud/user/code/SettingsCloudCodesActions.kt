package com.github.enteraname74.soulsearching.feature.settings.cloud.user.code

import com.github.enteraname74.domain.model.user.UserInscriptionCode

interface SettingsCloudCodesActions {
    fun navigateBack()
    fun generateCode()
    fun deleteCode(code: UserInscriptionCode)
}
