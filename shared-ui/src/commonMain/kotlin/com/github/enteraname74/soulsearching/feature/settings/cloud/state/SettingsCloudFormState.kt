package com.github.enteraname74.soulsearching.feature.settings.cloud.state

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldDefaults
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle

sealed interface SettingsCloudFormState {
    data object Loading : SettingsCloudFormState
    data class Data(
        val username: String,
        val password: String,
        val isSignIn: Boolean,
        val error: String?,
    ): SettingsCloudFormState {
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
                )
            )

            if (isSignIn) {
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
                    )
                )
            }
        }

        fun getFormUsername(): String = textFields.find { it.id == USERNAME_ID }?.value.orEmpty()
        fun getFormPassword(): String = textFields.find { it.id == PASSWORD_ID }?.value.orEmpty()
        fun getInscriptionCode(): String = textFields.find { it.id == INSCRIPTION_CODE_ID }?.value.orEmpty()

        fun isValid(): Boolean = textFields.all { it.isValid() }
    }

    companion object {
        const val USERNAME_ID = "USERNAME_ID"
        const val PASSWORD_ID = "PASSWORD_ID"
        const val INSCRIPTION_CODE_ID = "INSCRIPTION_CODE_ID"
    }
}
