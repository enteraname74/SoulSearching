package com.github.enteraname74.soulsearching.feature.settings.cloud.state

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.*

sealed interface SettingsCloudSignInFormState {
    data object Loading : SettingsCloudSignInFormState
    data class Data(
        val username: String,
        val password: String,
        val error: String?,
        val onScanQrCode: () -> Unit,
    ): SettingsCloudSignInFormState {
        val textFields: List<SoulTextFieldHolder> = buildList {
            add(
                SoulTextFieldHolderImpl(
                    modifier = Modifier
                        .fillMaxWidth(),
                    id = USERNAME_ID,
                    initialValue = username,
                    isValid = { it.isNotBlank() },
                    getLabel = { strings.cloudUsername },
                    style = SoulTextFieldStyle.Top,
                    getError = { strings.fieldCannotBeEmpty },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                    ),
                    getColors = {
                        SoulTextFieldDefaults.primaryColors()
                    }
                )
            )
            add(
                SoulTextFieldHolderImpl(
                    modifier = Modifier
                        .fillMaxWidth(),
                    id = PASSWORD_ID,
                    initialValue = password,
                    isValid = { it.isNotBlank() },
                    getLabel = { strings.cloudPassword },
                    style = SoulTextFieldStyle.Body,
                    getError = { strings.fieldCannotBeEmpty },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password,
                    ),
                    getColors = {
                        SoulTextFieldDefaults.primaryColors()
                    },
                    isPassword = true,
                )
            )

            add(
                SoulTextFieldHolderImpl(
                    modifier = Modifier
                        .fillMaxWidth(),
                    id = INSCRIPTION_CODE_ID,
                    initialValue = "",
                    isValid = { it.isNotBlank() },
                    getLabel = { strings.cloudSignInCode },
                    style = SoulTextFieldStyle.Bottom,
                    getError = { strings.fieldCannotBeEmpty },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password,
                    ),
                    getColors = {
                        SoulTextFieldDefaults.primaryColors()
                    },
                    isPassword = true,
                    leadingIconSpec = SoulTextFieldLeadingIconSpec(
                        icon = Icons.Rounded.QrCodeScanner,
                        onClick = onScanQrCode,
                    )
                )
            )
        }

        fun getFormUsername(): String = textFields.find { it.id == USERNAME_ID }?.value.orEmpty()
        fun getFormPassword(): String = textFields.find { it.id == PASSWORD_ID }?.value.orEmpty()
        fun getInscriptionCode(): String = textFields.find { it.id == INSCRIPTION_CODE_ID }?.value.orEmpty()

        fun setInscriptionCode(inscriptionCode: String) {
            textFields.find { it.id == INSCRIPTION_CODE_ID }?.onValueChanged(inscriptionCode)
        }

        fun isValid(): Boolean = textFields.all { it.isValid() }
    }

    companion object {
        const val USERNAME_ID = "USERNAME_ID"
        const val PASSWORD_ID = "PASSWORD_ID"
        const val INSCRIPTION_CODE_ID = "INSCRIPTION_CODE_ID"
    }
}
