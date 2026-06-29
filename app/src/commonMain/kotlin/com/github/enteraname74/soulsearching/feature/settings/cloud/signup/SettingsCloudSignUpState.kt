package com.github.enteraname74.soulsearching.feature.settings.cloud.signup

import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

data class SettingsCloudSignUpState(
    val nameField: SoulTextFieldHolder,
    val passwordField: SoulTextFieldHolder,
    val codeField: SoulTextFieldHolder,
) {
    fun isValid(): Boolean =
        nameField.isValid() && passwordField.isValid() && codeField.isValid()
}
