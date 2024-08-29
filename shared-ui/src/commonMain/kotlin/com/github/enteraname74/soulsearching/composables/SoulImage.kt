package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.coversprovider.ImageCoverRetriever
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.saxophone_svg
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.util.*
import com.github.enteraname74.soulsearching.shared_ui.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SoulImage(
    coverId: UUID?,
    size: Dp,
    modifier: Modifier = Modifier,
    roundedPercent: Int = 10,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    imageCoverRetriever: ImageCoverRetriever = injectElement(),
) {
    val bitmap: ImageBitmap? by imageCoverRetriever.getImageBitmap(coverId = coverId).collectAsState(
        imageCoverRetriever.getDefaultImageBitmap(coverId = coverId)
    )

    val modifierBase = Modifier
        .size(size)
        .clip(RoundedCornerShape(percent = roundedPercent))
        .composed {
            modifier
        }

    bitmap?.let {
        Image(
            modifier = modifierBase,
            bitmap = it,
            contentDescription = strings.image,
            contentScale = ContentScale.Crop
        )
    } ?: Image(
        modifier = modifierBase,
        painter = painterResource(Res.drawable.saxophone_svg),
        contentDescription = strings.image,
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(tint)
    )
}