package data.remote

import androidx.paging.PagingData
import app.cash.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import domain.model.*
import graphql.VideoDetailsQuery
import graphql.VideosQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VideoRemoteApi(private val apolloClient: ApolloClient) {

    suspend fun loadDetails(id: String): Result<VideoDetailsEntity> {
        return withContext(Dispatchers.IO) {

            val result = apolloClient.query(VideoDetailsQuery(id)).execute()

            when {
                !result.hasErrors() -> {
                    result.dataOrThrow().videos?.edges?.firstOrNull()?.node?.let { video ->
                        Result.success(
                            VideoDetailsEntity(
                                id = video.id.toString(),
                                title = video.title,
                                photo = video.photoLink ?: "",
                                createdAt = video.createdAt.toString(),
                                actresses = video.actresses?.mapNotNull { actress ->
                                    actress?.let { act ->
                                        ActressEntity(
                                            id = act.id.toString(),
                                            name = act.name,
                                            photo = act.photoLink ?: "",
                                            isFavorite = false
                                        )
                                    }
                                } ?: emptyList(),
                                tags = video.tags?.mapNotNull { tag ->
                                    tag?.let { t ->
                                        TagEntity(
                                            id = t.id.toString(),
                                            name = t.name,
                                            isFavorite = false
                                        )
                                    }
                                } ?: emptyList(),
                                players = video.players?.mapNotNull { player ->
                                    player?.let { p ->
                                        PlayerEntity(
                                            id = p.id.toString(),
                                            playerLink = p.playerLink,
                                            postedAt = p.createdAt.toString(),
                                            isWorking = p.isWorking,
                                        )
                                    }
                                } ?: emptyList(),
                            )
                        )
                    } ?: Result.failure(Exception("Received a ${result.errors}."))
                }

                result.hasErrors() -> {
                    Result.failure(Exception("Received a ${result.errors}."))
                }

                else -> {
                    Result.failure(Exception("Received a ${result.errors}."))
                }
            }
        }
    }

    suspend fun loadAllRecentVideos(): Flow<PagingData<VideoEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(pagingConfig) {
                RecentVideosPagingSource(apolloClient)
            }.flow
        }
    }

    private class RecentVideosPagingSource(
        private val apolloClient: ApolloClient,
    ) : PagingSource<String, VideoEntity>() {
        override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, VideoEntity> {
            val page = params.key ?: FIRST_PAGE_INDEX

            val result = withContext(Dispatchers.IO) {
                apolloClient.query(
                    when (params) {
                        is PagingSourceLoadParamsRefresh<String> -> {
                            VideosQuery(afterSize = Optional.present(40))
                        }

                        is PagingSourceLoadParamsAppend<String> -> {
                            VideosQuery(afterSize = Optional.present(40), cursorEnd = Optional.present(params.key))
                        }

                        is PagingSourceLoadParamsPrepend<String> -> {
                            VideosQuery(beforeSize = Optional.present(40), cursorStart = Optional.present(params.key))
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
}