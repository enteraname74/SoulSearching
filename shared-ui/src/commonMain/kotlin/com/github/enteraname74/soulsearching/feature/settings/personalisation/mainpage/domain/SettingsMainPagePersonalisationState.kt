package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain

import com.github.enteraname74.soulsearching.domain.model.ElementsVisibility

sealed interface SettingsMainPagePersonalisationState {
    data object Loading: SettingsMainPagePersonalisationState
    data class Data(
        val elementsVisibility: ElementsVisibility,
    ): SettingsMainPagePersonalisationState
}
