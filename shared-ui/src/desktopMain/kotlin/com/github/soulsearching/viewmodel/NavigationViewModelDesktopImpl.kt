package com.github.soulsearching.viewmodel

import com.github.soulsearching.viewmodel.handler.NavigationViewModelHandler

class NavigationViewModelDesktopImpl: NavigationViewModel {
    override val handler: NavigationViewModelHandler = NavigationViewModelHandler()
}