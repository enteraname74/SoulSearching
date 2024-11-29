package com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain

import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.max

class SettingsPlayerPersonalisationViewModel(
    private val settings: SoulSearchingSettings,
) : ScreenModel {
    val state: StateFlow<SettingsPlayerPersonalisationState> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Player.IS_PLAYER_SWIPE_ENABLED),
        settings.getFlowOn(SoulSearchingSettingsKeys.Player.IS_REWIND_ENABLED),
        settings.getFlowOn(SoulSearchingSettingsKeys.Player.IS_MINIMISED_SONG_PROGRESSION_SHOWN),
    ) { isPlayerSwipeEnabled, isRewindEnabled, isMinimisedSongProgressionEnabled ->
        println("REWIND? $isRewindEnabled")
        SettingsPlayerPersonalisationState(
            isPlayerSwipeEnabled = isPlayerSwipeEnabled,
            isRewindEnabled = isRewindEnabled,
            isMinimisedSongProgressionShown = isMinimisedSongProgressionEnabled,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsPlayerPersonalisationState(
            isPlayerSwipeEnabled = SoulSearchingSettingsKeys.Player.IS_PLAYER_SWIPE_ENABLED.defaultValue,
            isRewindEnabled = SoulSearchingSettingsKeys.Player.IS_REWIND_ENABLED.defaultValue,
            isMinimisedSongProgressionShown =
            SoulSearchingSettingsKeys.Player.IS_MINIMISED_SONG_PROGRESSION_SHOWN.defaultValue,
        )
    )

    val soulMixTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = SOUL_MIX_TEXT_FIELD,
        initialValue = settings.get(SoulSearchingSettingsKeys.Player.SOUL_MIX_TOTAL_BY_LIST).toString(),
        isValid = { text -> text.isNotBlank() },
        keyboardType = KeyboardType.Number,
        getLabel = { null },
        onValueChange = { text ->
            val intValue = text.toIntOrNull() ?: return@SoulTextFieldHolderImpl
            settings.set(
                key = SoulSearchingSettingsKeys.Player.SOUL_MIX_TOTAL_BY_LIST.key,
                value = max(1, intValue),
            )
        },
        getError = { strings.fieldCannotBeEmpty },
    )

    fun togglePlayerSwipe() {
        settings.set(
            key = SoulSearchingSettingsKeys.Player.IS_PLAYER_SWIPE_ENABLED.key,
            value = !state.value.isPlayerSwipeEnabled,
        )
    }

    fun toggleRewind() {
        settings.set(
            key = SoulSearchingSettingsKeys.Player.IS_REWIND_ENABLED.key,
            value = !state.value.isRewindEnabled,
        )
    }

    fun toggleMinimisedProgression() {
        settings.set(
            key = SoulSearchingSettingsKeys.Player.IS_MINIMISED_SONG_PROGRESSION_SHOWN.key,
            value = !state.value.isMinimisedSongProgressionShown,
        )
    }

    companion object {
        private const val SOUL_MIX_TEXT_FIELD = "SOUL_MIX_TEXT_FIELD"
    }
}