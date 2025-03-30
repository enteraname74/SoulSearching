package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.composables.TemplateImage
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
internal fun UrlImage(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String?,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {

    val settings: SoulSearchingSettings = injectElement()

    val host: String by settings
        .getFlowOn(SoulSearchingSettingsKeys.Cloud.HOST)
        .collectAsState(initial = "")

    val accessToken: String by settings
        .getFlowOn(SoulSearchingSettingsKeys.Cloud.ACCESS_TOKEN)
        .collectAsState(initial = "")

    AnimatedImage(
        data = url,
        contentScale = contentScale,
        modifier = modifier,
        tint = tint,
    ) { foundUrl ->
        if (host.isEmpty() || accessToken.isEmpty()) {
            TemplateImage(
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
            )
        } else {
            SoulDataImage(
                data = "$host/$foundUrl",
                contentScale = contentScale,
                modifier = modifier,
                onSuccess = onSuccess,
                builderOptions = {
                    builderOptions()
                        .httpHeaders(
                            NetworkHeaders
                                .Builder()
                                .set(
                                    key = "Authorization",
                                    value = "Bearer $accessToken"
                                )
                                .build()
                        )
                },
                contentDescription = contentDescription,
                tint = tint,
            )
        }
    }
}