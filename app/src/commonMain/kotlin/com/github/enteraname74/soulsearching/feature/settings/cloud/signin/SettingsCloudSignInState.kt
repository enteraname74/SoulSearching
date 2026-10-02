package com.github.enteraname74.soulsearching.feature.settings.cloud.signin

import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

data class SettingsCloudSignInState(
    val nameField: SoulTextFieldHolder,
    val passwordField: SoulTextFieldHolder,
) {
    fun isValid(): Boolean =
        nameField.isValid() && passwordField.isValid()
}
