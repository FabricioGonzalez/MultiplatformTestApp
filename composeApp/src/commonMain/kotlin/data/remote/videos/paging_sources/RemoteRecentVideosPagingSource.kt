package data.remote.videos.paging_sources

import app.cash.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import domain.model.VideoEntity
import graphql.VideosQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


internal class RemoteRecentVideosPagingSource(
    private val apolloClient: ApolloClient,
) : PagingSource<String, VideoEntity>() {

    override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, VideoEntity> {
        val result = withContext(Dispatchers.IO) {
            apolloClient.query(
                when (params) {
                    is PagingSourceLoadParamsRefresh<String> -> {
                        VideosQuery(afterSize = Optional.present(40))
                    }

                    is PagingSourceLoadParamsAppend<String> -> {
                        VideosQuery(
                            afterSize = Optional.present(40), cursorEnd = Optional.present(params.key)
                        )
                    }

                    is PagingSourceLoadParamsPrepend<String> -> {
                        VideosQuery(
                            beforeSize = Optional.present(40), cursorStart = Optional.present(params.key)
                        )
                    }

                }
            ).fetchPolicy(FetchPolicy.CacheFirst).execute()

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

