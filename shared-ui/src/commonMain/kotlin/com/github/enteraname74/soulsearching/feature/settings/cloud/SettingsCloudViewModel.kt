package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.usecase.auth.*
import com.github.enteraname74.domain.usecase.cloud.GetCloudSearchMetadataUseCase
import com.github.enteraname74.domain.usecase.cloud.ResetAndSyncDataWithCloudUseCase
import com.github.enteraname74.domain.usecase.cloud.SetCloudSearchMetadataUseCase
import com.github.enteraname74.domain.usecase.cloud.SyncDataWithCloudUseCase
import com.github.enteraname74.domain.usecase.datamode.GetCurrentDataModeUseCase
import com.github.enteraname74.domain.usecase.datamode.SetCurrentDataModeUseCase
import com.github.enteraname74.domain.usecase.music.GetCloudUploadMusicUseCase
import com.github.enteraname74.domain.usecase.music.UploadAllMusicToCloudUseCase
import com.github.enteraname74.domain.util.FlowResult
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldDefaults
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.feature.settings.cloud.composable.SettingsCloudCodeDialog
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudUploadState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

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
    private val playbackManager: PlaybackManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : ScreenModel, KoinComponent, SettingsCloudListener {
    private val errorInSign: MutableStateFlow<String?> = MutableStateFlow(null)
    private val errorInLog: MutableStateFlow<String?> = MutableStateFlow(null)
    private val getCloudSearchMetadataUseCase: GetCloudSearchMetadataUseCase by inject()
    private val setCloudSearchMetadataUseCase: SetCloudSearchMetadataUseCase by inject()
    private val getCloudUploadMusicUseCase: GetCloudUploadMusicUseCase by inject()
    private val uploadAllMusicToCloudUseCase: UploadAllMusicToCloudUseCase by inject()
    private val generateInscriptionCodeUseCase: GenerateInscriptionCodeUseCase by inject()

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

    val uploadState: StateFlow<SettingsCloudUploadState> =
        combine(
            getCloudSearchMetadataUseCase(),
            getCloudUploadMusicUseCase()
        ) { searchMetadata, uploadMusic ->
            when (uploadMusic) {
                is FlowResult.Error -> SettingsCloudUploadState.Error(
                    error = uploadMusic.message,
                    searchMetadata = searchMetadata,
                )
                is FlowResult.Loading -> SettingsCloudUploadState.Uploading(
                    progress = uploadMusic.progress ?: 0f,
                    searchMetadata = searchMetadata,
                )
                is FlowResult.Success -> SettingsCloudUploadState.Idle(
                    searchMetadata = searchMetadata,
                )
            }
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsCloudUploadState.Idle(searchMetadata = false),
        )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val HOST_TEXT_FIELD = "HOST_TEXT_FIELD"
    private var hostJob: Job? = null
    val hostTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = HOST_TEXT_FIELD,
        initialValue = runBlocking { getCloudHostUseCase().first() },
        isValid = { it.isNotBlank() },
        getLabel = { strings.cloudHost },
        getError = { strings.fieldCannotBeEmpty },
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val signInFormState: StateFlow<SettingsCloudFormState> =
        errorInLog.mapLatest { error ->
            SettingsCloudFormState.Data(
                username = "",
                password = "",
                error = error,
                isSignIn = true,
            )
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsCloudFormState.Loading,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val logInFormState: StateFlow<SettingsCloudFormState> =
        errorInSign.mapLatest { error ->
            SettingsCloudFormState.Data(
                username = "",
                password = "",
                error = error,
                isSignIn = false,
            )
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsCloudFormState.Loading,
        )

    override fun signIn() {
        val validForm = (signInFormState.value as? SettingsCloudFormState.Data)?.takeIf { it.isValid() } ?: return

        loadingManager.withLoadingOnIO {
            val result: SoulResult<*> = signUserUseCase(
                user = User(
                    username = validForm.getFormUsername(),
                    password = validForm.getFormPassword(),
                ),
                inscriptionCode = validForm.getInscriptionCode()
            )
            when (result) {
                is SoulResult.Error -> {
                    errorInSign.value = result.error
                }

                is SoulResult.Success<*> -> {
                    errorInLog.value = null
                    errorInSign.value = null
                    CoroutineScope(Dispatchers.IO).launch {
                        val syncData: SoulResult<List<UUID>> = syncDataWithCloudUseCase()
                        (syncData as? SoulResult.Success)?.data?.takeIf { it.isNotEmpty() }?.let { deletedIds ->
                            playbackManager.removeSongsFromPlayedPlaylist(deletedIds)
                            feedbackPopUpManager.showFeedback(
                                feedback = strings.deleteMusicsFromCloudAutomatically(
                                    total = deletedIds.size,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    override fun logOut() {
        CoroutineScope(Dispatchers.IO).launch {
            logOutUserUseCase()
        }
    }

    override fun logIn() {
        val dataState = (state.value as? SettingsCloudState.Data) ?: return
        val validForm = (logInFormState.value as? SettingsCloudFormState.Data)?.takeIf { it.isValid() } ?: return

        loadingManager.withLoadingOnIO {
            val newUserInformation = User(
                username = validForm.getFormUsername(),
                password = validForm.getFormPassword(),
            )

            val isDifferentFromCurrentUser = newUserInformation.username != dataState.user?.username

            val result: SoulResult<*> = logInUserUseCase(
                user = newUserInformation
            )

            when (result) {
                is SoulResult.Error -> {
                    errorInLog.value = result.error
                }

                is SoulResult.Success<*> -> {
                    errorInLog.value = null
                    errorInSign.value = null

                    CoroutineScope(Dispatchers.IO).launch {
                        /*
                        If the user is different from the previous one, we reset all cloud data before syncing with it.
                         */
                        val syncResult: SoulResult<List<UUID>> = if (isDifferentFromCurrentUser) {
                            resetAndSyncDataWithCloudUseCase()
                        } else {
                            syncDataWithCloudUseCase()
                        }

                        (syncResult as? SoulResult.Success)?.data?.takeIf { it.isNotEmpty() }?.let { deletedIds ->
                            playbackManager.removeSongsFromPlayedPlaylist(deletedIds)
                            feedbackPopUpManager.showFeedback(
                                feedback = strings.deleteMusicsFromCloudAutomatically(
                                    total = deletedIds.size,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun toggleCloudState() {
        val dataState = (state.value as? SettingsCloudState.Data) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            setCurrentDataModeUSeCase(
                dataMode = if (dataState.isCloudActivated) DataMode.Local else DataMode.Cloud,
            )
        }
    }

    override fun toggleSearchMetadata() {
        CoroutineScope(Dispatchers.IO).launch {
            setCloudSearchMetadataUseCase(
                searchMetadata = !uploadState.value.searchMetadata,
            )
        }
    }


    override fun uploadAllMusicToCloud() {
        CoroutineScope(Dispatchers.IO).launch {
            uploadAllMusicToCloudUseCase()
        }
    }

    override fun generateCode() {
        loadingManager.withLoadingOnIO {
            val result: SoulResult<CloudInscriptionCode> = generateInscriptionCodeUseCase()

            (result as? SoulResult.Success)?.data?.let { code ->
                _dialogState.value = SettingsCloudCodeDialog(
                    onDismiss = { _dialogState.value = null },
                    inscriptionCode = code,
                )
            }
            feedbackPopUpManager.showResultErrorIfAny(result)
        }
    }
}