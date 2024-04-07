package com.github.soulsearching.mainpage.domain

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.mainpage.presentation.MainPageScreen
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom serializer for the MainPageScreen.
 */
object MainPageSerializer: KSerializer<MainPageScreen> {

    @OptIn(ExperimentalMaterialApi::class)
    override fun deserialize(decoder: Decoder): MainPageScreen {
        val string = decoder.decodeString()
        val searchValue = BottomSheetStates.valueOf(string.split("+")[0])
        val playerValue = BottomSheetStates.valueOf(string.split("+")[1])
        val searchDraggableState = SwipeableState(initialValue = searchValue)
        val playerDraggableState = SwipeableState(initialValue = playerValue)

        return MainPageScreen(
            playerDraggableState = playerDraggableState,
            searchDraggableState = searchDraggableState
        )
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "MainPageScreen",
        PrimitiveKind.STRING
    )

    @OptIn(ExperimentalMaterialApi::class)
    override fun serialize(encoder: Encoder, value: MainPageScreen) {
        val string = "${value.searchDraggableState.currentValue}+${value.playerDraggableState.currentValue}"
        encoder.encodeString(string)
    }
}