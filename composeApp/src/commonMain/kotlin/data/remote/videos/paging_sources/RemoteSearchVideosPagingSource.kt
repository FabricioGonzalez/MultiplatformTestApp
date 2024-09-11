package data.remote.videos.paging_sources

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadParamsAppend
import app.cash.paging.PagingSourceLoadParamsPrepend
import app.cash.paging.PagingSourceLoadParamsRefresh
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import com.apollographql.apollo3.api.Optional
import data.ApolloConnector
import domain.model.VideoEntity
import graphql.SearchVideosByTermQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class RemoteSearchVideosPagingSource(
    private val apolloClient: ApolloConnector, private val search: String
) : PagingSource<String, VideoEntity>() {

    override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, VideoEntity> {
        val result = withContext(Dispatchers.IO) {
            apolloClient.query(
                when (params) {
                    is PagingSourceLoadParamsRefresh<String> -> {
                        SearchVideosByTermQuery(
                            afterSize = Optional.present(40),
                            videoQuery = search,
                            includeDeleted = Optional.present(false)
                        )
                    }

                    is PagingSourceLoadParamsAppend<String> -> {
                        SearchVideosByTermQuery(
                            afterSize = Optional.present(40),
                            cursorEnd = Optional.present(params.key),
                            videoQuery = search,
                            includeDeleted = Optional.present(false)
                        )
                    }

                    is PagingSourceLoadParamsPrepend<String> -> {
                        SearchVideosByTermQuery(
                            beforeSize = Optional.present(40),
                            cursorStart = Optional.present(params.key),
                            videoQuery = search,
                            includeDeleted = Optional.present(false)
                        )
                    }

                }
            )
        }

        return when {
            !result.hasErrors() -> {
                result.dataOrThrow().videos?.let { videos ->
                    PagingSourceLoadResultPage(
                        prevKey = null, nextKey = videos.pageInfo.endCursor,
                        data = videos.edges?.map { entity ->
                            VideoEntity(
                                cursor = entity.cursor,
                                photo = entity.node?.photoLink ?: "",
                                title = entity.node?.title ?: "",
                                id = entity.node?.id.toString()
                            )
                        } ?: emptyList(),
                    )

                } ?: PagingSourceLoadResultError(
                    Exception("Received a ${result.errors}."),
                )
            }

            result.hasErrors() -> {
                PagingSourceLoadResultError(
                    Exception("Received a ${result.errors}."),
                )
            }

            else -> {
                PagingSourceLoadResultError(
                    Exception("Received a ${result.errors}."),
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, VideoEntity>): String? = null
}
