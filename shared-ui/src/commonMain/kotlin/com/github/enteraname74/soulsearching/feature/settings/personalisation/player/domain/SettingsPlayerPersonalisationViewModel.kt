package com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain

import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.math.max

class SettingsPlayerPersonalisationViewModel(
    private val settings: SoulSearchingSettings,
): ScreenModel {
    val isPlayerSwipeEnabled: StateFlow<Boolean> = settings.getFlowOn(
        key = SoulSearchingSettings.IS_PLAYER_SWIPE_ENABLED,
        defaultValue = SoulSearchingSettings.IS_PLAYER_SWIPE_ENABLED_DEFAULT
    ).stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SoulSearchingSettings.IS_PLAYER_SWIPE_ENABLED_DEFAULT
    )

    val soulMixTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = SOUL_MIX_TEXT_FIELD,
        initialValue = settings.getInt(
            key = SoulSearchingSettings.SOUL_MIX_TOTAL_BY_LIST,
            defaultValue = SoulSearchingSettings.SOUL_MIX_TOTAL_BY_LIST_DEFAULT,
        ).toString(),
        isValid = { text -> text.isNotBlank() },
        keyboardType = KeyboardType.Number,
        getLabel = { null },
        onValueChange = { text ->
            val intValue = text.toIntOrNull() ?: return@SoulTextFieldHolderImpl
            settings.setInt(
                key = SoulSearchingSettings.SOUL_MIX_TOTAL_BY_LIST,
                value = max(1, intValue),
            )
        }
    )

    fun togglePlayerSwipe() {
        settings.setBoolean(
            key = SoulSearchingSettings.IS_PLAYER_SWIPE_ENABLED,
            value = !isPlayerSwipeEnabled.value
        )
    }

    companion object {
        private const val SOUL_MIX_TEXT_FIELD = "SOUL_MIX_TEXT_FIELD"
    }
}