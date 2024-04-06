package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.soulsearching.domain.viewmodel.MainActivityViewModel
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.viewmodelhandler.MainActivityViewModelHandler

/**
 * Implementation of the MainActivityViewModel.
 */
class MainActivityViewModelAndroidImpl(
    settings: SoulSearchingSettings
) : MainActivityViewModel, ViewModel() {
    override val handler: MainActivityViewModelHandler = MainActivityViewModelHandler(
        settings = settings
    )
}