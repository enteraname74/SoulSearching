package com.github.enteraname74.soulsearching.domain.viewmodel.handler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.enteraname74.soulsearching.domain.navigation.NavigationController
import com.github.enteraname74.soulsearching.domain.navigation.Route
import com.github.enteraname74.soulsearching.domain.navigation.RoutesNames

class NavigationViewModelHandler: ViewModelHandler {
    var navigationController by mutableStateOf(
        NavigationController(
            initialRoute = Route(
                RoutesNames.MAIN_PAGE_SCREEN
            )
        )
    )
}