package com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

class SettingsPlayerPersonalisationViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val state: StateFlow<SettingsPlayerPersonalisationState> = settingsRepository.observePlayer().map { settings ->
        SettingsPlayerPersonalisationState(
            isPlayerSwipeEnabled = settings.isPlayerSwipeEnabled,
            isRewindEnabled = settings.isRewindEnabled,
            isMinimisedSongProgressionShown = settings.isMinimisedSongProgressionShown,
            playerVolume = settings.playerVolume,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsPlayerPersonalisationState(
            isPlayerSwipeEnabled = true,
            isRewindEnabled = true,
            isMinimisedSongProgressionShown = true,
            playerVolume = 1f,
        )
    )

    val soulMixTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = SOUL_MIX_TEXT_FIELD,
        initialValue = "50",
        isValid = { text -> text.isNotBlank() },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        getLabel = { null },
        onValueChange = { text ->
            val intValue = text.toIntOrNull() ?: return@SoulTextFieldHolderImpl
            viewModelScope.launch { settingsRepository.setSoulMixTotalByList(max(1, intValue)) }
        },
        getError = { CoreRes.string.field_cannot_be_empty.toTextSpec() },
    )

    init {
        viewModelScope.launch {
            settingsRepository.observePlayer().collect { settings ->
                soulMixTextField.updateValue(settings.soulMixTotalByList.toString())
            }
        }
    }

    fun togglePlayerSwipe() {
        viewModelScope.launch { settingsRepository.setPlayerSwipeEnabled(!state.value.isPlayerSwipeEnabled) }
    }

    fun toggleRewind() {
        viewModelScope.launch { settingsRepository.setRewindEnabled(!state.value.isRewindEnabled) }
    }

    fun toggleMinimisedProgression() {
        viewModelScope.launch { settingsRepository.setMinimisedSongProgressionShown(!state.value.isMinimisedSongProgressionShown) }
    }

    fun setVolumePlayer(newVolume: Float) {
        viewModelScope.launch { settingsRepository.setPlayerVolume(newVolume) }
    }

    companion object {
        private const val SOUL_MIX_TEXT_FIELD = "SOUL_MIX_TEXT_FIELD"
    }
}
