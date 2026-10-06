package com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state

sealed interface ModifyCollectionNavigationState {
    data object Idle : ModifyCollectionNavigationState
    data object Back : ModifyCollectionNavigationState
}
