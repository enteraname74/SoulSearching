package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.soulsearching.viewmodel.handler.NavigationViewModelHandler

class NavigationViewModelAndroidImpl: ViewModel(), NavigationViewModel {
    override val handler: NavigationViewModelHandler = NavigationViewModelHandler()
}