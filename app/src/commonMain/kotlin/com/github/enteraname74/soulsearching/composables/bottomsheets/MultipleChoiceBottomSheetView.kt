package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun MultipleChoiceBottomSheetView(
    topInformation: BottomSheetTopInformation,
    choices: List<BottomSheetRowSpec>,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
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
