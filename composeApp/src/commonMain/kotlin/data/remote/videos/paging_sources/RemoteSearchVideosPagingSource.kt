package data.remote.videos.paging_sources

import app.cash.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import domain.model.VideoEntity
import graphql.SearchVideosByTermQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class RemoteSearchVideosPagingSource(
    private val apolloClient: ApolloClient, private val search: String
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
            ).execute()
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
