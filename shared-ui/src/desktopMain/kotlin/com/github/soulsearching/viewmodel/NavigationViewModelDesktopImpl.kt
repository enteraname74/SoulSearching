package com.github.soulsearching.viewmodel

import com.github.soulsearching.domain.viewmodel.NavigationViewModel
import com.github.soulsearching.domain.viewmodel.handler.NavigationViewModelHandler

class NavigationViewModelDesktopImpl: NavigationViewModel {
    override val handler: NavigationViewModelHandler = NavigationViewModelHandler()
}