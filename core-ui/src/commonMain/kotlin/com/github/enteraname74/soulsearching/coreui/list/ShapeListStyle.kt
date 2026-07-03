package com.github.enteraname74.soulsearching.coreui.list

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ShapeListStyle {
    Top,
    Body,
    Unique,
    Bottom;

    fun shape(): Shape =
        when (this) {
            Top -> RoundedCornerShape(
                topStart = CORNER_BIG,
                topEnd = CORNER_BIG,
                bottomStart = CORNER_SMALL,
                bottomEnd = CORNER_SMALL,
            )
            Unique -> RoundedCornerShape(
                size = CORNER_BIG,
            )
            Body -> RoundedCornerShape(
                size = CORNER_SMALL,
            )
            Bottom -> RoundedCornerShape(
                topStart = CORNER_SMALL,
                topEnd = CORNER_SMALL,
                bottomStart = CORNER_BIG,
                bottomEnd = CORNER_BIG,
            )
        }

    companion object {
        fun fromList(
            listSize: Int,
            index: Int,
        ) : ShapeListStyle =
            when {
                listSize == 1 -> Unique
                index == 0 -> Top
                index == listSize - 1 -> Bottom
                else -> Body
            }
    }
}

private val CORNER_BIG: Dp = 16.dp
private val CORNER_SMALL: Dp = 6.dp
