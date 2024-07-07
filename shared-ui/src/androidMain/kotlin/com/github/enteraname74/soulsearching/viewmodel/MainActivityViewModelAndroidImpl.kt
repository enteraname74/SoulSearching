package com.github.enteraname74.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.enteraname74.soulsearching.domain.viewmodel.MainActivityViewModel
import com.github.enteraname74.domain.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler.MainActivityViewModelHandler

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