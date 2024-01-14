package com.github.soulsearching.viewmodel

import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.MainActivityViewModelHandler

/**
 * Implementation of the MainActivityViewModel.
 */
class MainActivityViewModelDesktopImpl(
    settings: SoulSearchingSettings
) : MainActivityViewModel {
    override val handler: MainActivityViewModelHandler = MainActivityViewModelHandler(
        settings = settings
    )
}