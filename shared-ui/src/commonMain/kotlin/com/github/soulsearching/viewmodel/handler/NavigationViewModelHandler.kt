package com.github.soulsearching.viewmodel.handler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.soulsearching.navigation.NavigationController
import com.github.soulsearching.navigation.Route
import com.github.soulsearching.navigation.RoutesNames

class NavigationViewModelHandler: ViewModelHandler {
    var navigationController by mutableStateOf(
        NavigationController(
            initialRoute = Route(
                RoutesNames.MAIN_PAGE_SCREEN
            )
        )
    )
}