package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.MainActivityViewModelHandler

/**
 * Implementation of the MainActivityViewModel.
 */
class MainActivityViewModelAndroidImpl(
    settings: SoulSearchingSettings
) : ViewModel(), MainActivityViewModel {
    override val handler: MainActivityViewModelHandler = MainActivityViewModelHandler(
        settings = settings
    )
}