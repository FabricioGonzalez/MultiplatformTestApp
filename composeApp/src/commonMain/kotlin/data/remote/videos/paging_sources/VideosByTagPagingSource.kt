package data.remote.videos.paging_sources

import app.cash.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import domain.model.VideoEntity
import graphql.VideosByTagQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


internal class VideosByTagPagingSource(
    private val apolloClient: ApolloClient, private val tagName: String
) : PagingSource<String, VideoEntity>() {
    override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, VideoEntity> {
        val page = params.key ?: FIRST_PAGE_INDEX

        val result = withContext(Dispatchers.IO) {
            apolloClient.query(
                when (params) {
                    is PagingSourceLoadParamsRefresh<String> -> {
                        VideosByTagQuery(afterSize = Optional.present(40), name = tagName)
                    }

                    is PagingSourceLoadParamsAppend<String> -> {
                        VideosByTagQuery(
                            afterSize = Optional.present(40),
                            cursorEnd = Optional.present(params.key),
                            name = tagName
                        )
                    }

                    is PagingSourceLoadParamsPrepend<String> -> {
                        VideosByTagQuery(
                            beforeSize = Optional.present(40),
                            cursorStart = Optional.present(params.key),
                            name = tagName
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

    companion object {

        /**
         * The GitHub REST API uses [1-based page numbering](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#pagination).
         */
        const val FIRST_PAGE_INDEX = 1
    }
}
