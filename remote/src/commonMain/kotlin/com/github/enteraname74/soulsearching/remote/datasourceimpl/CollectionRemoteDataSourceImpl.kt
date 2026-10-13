package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.soulsearching.domain.model.CloudCollection
import com.github.enteraname74.soulsearching.domain.model.CollectionSyncData
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.remote.ext.appendCoverFile
import com.github.enteraname74.soulsearching.remote.ext.appendJson
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.CollectionIdsBody
import com.github.enteraname74.soulsearching.remote.model.upload.toCollectionUpload
import com.github.enteraname74.soulsearching.remote.resource.CollectionResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlin.uuid.Uuid

class CollectionRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val workDispatcher: WorkDispatcher,
) : CollectionRemoteDataSource {
    override suspend fun getDeletedRemoteCollectionIds(idsToCheck: List<Uuid>): List<Uuid> =
        client.withUrl(cloudPreferencesDataSource.getUrl()).post(CollectionResource.Check()) {
            contentType(ContentType.Application.Json)
            setBody(CollectionIdsBody(idsToCheck))
        }.body()

    override suspend fun getOfUser(
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?,
    ): List<CloudCollection> = client.withUrl(cloudPreferencesDataSource.getUrl()).get(
        CollectionResource.OfUser(
            lastUpdateAt = lastUpdateAt,
            maxPerPage = maxPerPage,
            page = page,
        )
    ).bodyOrThrow()

    override suspend fun deleteAll(ids: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        client.withUrl(cloudPreferencesDataSource.getUrl()).delete(CollectionResource()) {
            contentType(ContentType.Application.Json)
            setBody(ids.map(Uuid::toString))
        }
    }

    override suspend fun upload(collection: CollectionSyncData, coverPath: String?): CloudCollection =
        client.submitFormWithBinaryData(
            url = "${cloudPreferencesDataSource.getUrl()}/collection",
            formData = formData {
                appendJson("metadata", collection.toCollectionUpload())
                coverPath?.let { appendCoverFile("cover", it, workDispatcher) }
            }
        ) {
            method = HttpMethod.Post
        }.bodyOrThrow()
}
