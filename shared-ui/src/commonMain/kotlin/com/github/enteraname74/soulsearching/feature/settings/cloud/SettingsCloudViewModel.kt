package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.usecase.auth.*
import com.github.enteraname74.domain.usecase.cloud.ResetAndSyncDataWithCloudUseCase
import com.github.enteraname74.domain.usecase.cloud.SyncDataWithCloudUseCase
import com.github.enteraname74.domain.usecase.datamode.GetCurrentDataModeUseCase
import com.github.enteraname74.domain.usecase.datamode.SetCurrentDataModeUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldDefaults
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SettingsCloudViewModel(
    getCurrentDataModeUseCase: GetCurrentDataModeUseCase,
    getUserUseCase: GetUserUseCase,
    getCloudHostUseCase: GetCloudHostUseCase,
    private val signUserUseCase: SignUserUseCase,
    private val logInUserUseCase: LogInUserUseCase,
    private val logOutUserUseCase: LogOutUserUseCase,
    private val setCloudHostUseCase: SetCloudHostUseCase,
    private val setCurrentDataModeUSeCase: SetCurrentDataModeUseCase,
    private val syncDataWithCloudUseCase: SyncDataWithCloudUseCase,
    private val resetAndSyncDataWithCloudUseCase: ResetAndSyncDataWithCloudUseCase,
    private val loadingManager: LoadingManager,
) : ScreenModel {
    private val errorInSign: MutableStateFlow<String?> = MutableStateFlow(null)
    private val errorInLog: MutableStateFlow<String?> = MutableStateFlow(null)

    val state: StateFlow<SettingsCloudState> =
        combine(
            getCurrentDataModeUseCase(),
            getUserUseCase()
        ) { dataMode, user ->
            SettingsCloudState.Data(
                isCloudActivated = dataMode == DataMode.Cloud,
                user = user,
            )
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsCloudState.Loading,
        )

    private val HOST_TEXT_FIELD = "HOST_TEXT_FIELD"
    private var hostJob: Job? = null
    val hostTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = HOST_TEXT_FIELD,
        initialValue = runBlocking { getCloudHostUseCase().first() },
        isValid = { it.isNotBlank() },
        getLabel = { strings.cloudHost },
        getError = { strings.fieldCannotBeEmpty},
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        onChange = { host ->
            hostJob?.cancel()
            hostJob = CoroutineScope(Dispatchers.IO).launch {
                setCloudHostUseCase(host)
            }
        },
        getColors = {
            SoulTextFieldDefaults.primaryColors()
        }
    )

    val signInFormState: StateFlow<SettingsCloudFormState> =
        combine(
            getUserUseCase(),
            errorInSign,
        ) { user, error ->
            SettingsCloudFormState.Data(
                username = user?.username.orEmpty(),
                password = user?.password.orEmpty(),
                error = error,
            )
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsCloudFormState.Loading,
        )

    val logInFormState: StateFlow<SettingsCloudFormState> =
        combine(
            getUserUseCase(),
            errorInLog,
        ) { user, error ->
            SettingsCloudFormState.Data(
                username = user?.username.orEmpty(),
                password = user?.password.orEmpty(),
                error = error,
            )
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsCloudFormState.Loading,
        )

    private fun syncWithCloud() {
        CoroutineScope(Dispatchers.IO).launch {
            syncDataWithCloudUseCase()
        }
    }

    fun signIn() {
        val validForm = (signInFormState.value as? SettingsCloudFormState.Data)?.takeIf { it.isValid() } ?: return

        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                val result: SoulResult<*> = signUserUseCase(
                    user = User(
                        username = validForm.getFormUsername(),
                        password = validForm.getFormPassword(),
                    )
                )
                when(result) {
                    is SoulResult.Error -> {
                        errorInSign.value = result.error
                    }
                    is SoulResult.Success<*> -> {
                        errorInLog.value = null
                        errorInSign.value = null
                        syncDataWithCloudUseCase()
                    }
                }
            }
        }
    }

    fun logOut() {
        CoroutineScope(Dispatchers.IO).launch {
            logOutUserUseCase()
        }
    }

    fun logIn() {
        val dataState = (state.value as? SettingsCloudState.Data) ?: return
        val validForm = (logInFormState.value as? SettingsCloudFormState.Data)?.takeIf { it.isValid() } ?: return

        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                val newUserInformation = User(
                    username = validForm.getFormUsername(),
                    password = validForm.getFormPassword(),
                )

                val isDifferentFromCurrentUser = newUserInformation != dataState.user

                val result: SoulResult<*> = logInUserUseCase(
                    user = newUserInformation
                )
                when(result) {
                    is SoulResult.Error -> {
                        errorInLog.value = result.error
                    }
                    is SoulResult.Success<*> -> {
                        errorInLog.value = null
                        errorInSign.value = null


                        /*
                        If the user is different from the previous one, we reset all cloud data before syncing with it.
                         */
                        if (isDifferentFromCurrentUser) {
                            resetAndSyncDataWithCloudUseCase()
                        } else {
                            syncDataWithCloudUseCase()
                        }
                    }
                }
            }
        }
    }

    fun toggleCloudState() {
        val dataState = (state.value as? SettingsCloudState.Data) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            setCurrentDataModeUSeCase(
                dataMode = if (dataState.isCloudActivated) DataMode.Local else DataMode.Cloud,
            )
        }
    }
}