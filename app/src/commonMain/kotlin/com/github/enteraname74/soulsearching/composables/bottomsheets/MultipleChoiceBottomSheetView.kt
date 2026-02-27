package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
internal fun MultipleChoiceBottomSheetView(
    topInformation: BottomSheetTopInformation,
    choices: List<BottomSheetRowSpec>,
) {
    Column {
        BottomSheetElementInformation(
            title = topInformation.title,
            subTitle = topInformation.subTitle,
            cover = topInformation.cover,
        )
        choices.forEach {
            it.Row()
        }
    }
}