package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.github.enteraname74.domain.model.CloudPreferences
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
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
    commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase = injectElement(),
    commonUserUseCase: CommonUserUseCase = injectElement(),
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    val preferences: CloudPreferences? by commonCloudPreferencesUseCase
        .observePreferences()
        .collectAsStateWithLifecycle(null)

    val user: User? by commonUserUseCase
        .observeUser()
        .collectAsStateWithLifecycle(null)

    AnimatedImage(
        data = url,
        contentScale = contentScale,
        modifier = modifier,
        tint = tint,
    ) { foundUrl ->
        if (foundUrl.isBlank() || user?.accessToken == null || preferences?.url == null) {
            TemplateImage(
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
            )
        } else {
            SoulDataImage(
                data = "${preferences!!.url}/$foundUrl",
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
                                    value = "Bearer ${user?.accessToken}"
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