package com.github.enteraname74.soulsearching.viewmodel

import com.github.enteraname74.soulsearching.domain.viewmodel.NavigationViewModel
import com.github.enteraname74.soulsearching.domain.viewmodel.handler.NavigationViewModelHandler

class NavigationViewModelDesktopImpl: NavigationViewModel {
    override val handler: NavigationViewModelHandler = NavigationViewModelHandler()
}