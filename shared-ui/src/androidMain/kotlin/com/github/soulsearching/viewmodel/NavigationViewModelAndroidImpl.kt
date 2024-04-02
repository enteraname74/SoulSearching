package com.github.soulsearching.viewmodel

import com.github.soulsearching.viewmodel.handler.NavigationViewModelHandler

class NavigationViewModelAndroidImpl: NavigationViewModel {
    override val handler: NavigationViewModelHandler = NavigationViewModelHandler()
}