package data.remote

import androidx.paging.PagingSource
import app.cash.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import domain.model.ActressEntity
import graphql.ActressesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ActressRemoteApi(private val apolloClient: ApolloClient) {

    /*suspend fun loadDetails(id: String): Result<VideoDetailsEntity> {
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
    }*/

    suspend fun loadAllActresses(): Flow<PagingData<ActressEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(pagingConfig) {
                ActressesPagingSource(apolloClient)
            }.flow
        }
    }


    private class ActressesPagingSource(
        private val apolloClient: ApolloClient,
    ) : PagingSource<String, ActressEntity>() {
        override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, ActressEntity> {
            val page = params.key ?: FIRST_PAGE_INDEX

            val result = withContext(Dispatchers.IO) {
                apolloClient.query(
                    when (params) {
                        is PagingSourceLoadParamsRefresh<String> -> {
                            ActressesQuery(afterSize = Optional.present(40))
                        }

                        is PagingSourceLoadParamsAppend<String> -> {
                            ActressesQuery(afterSize = Optional.present(40), cursorEnd = Optional.present(params.key))
                        }

                        is PagingSourceLoadParamsPrepend<String> -> {
                            ActressesQuery(
                                beforeSize = Optional.present(40),
                                cursorStart = Optional.present(params.key)
                            )
                        }

                    }
                ).execute()
            }

            return when {
                !result.hasErrors() -> {
                    result.dataOrThrow().actresses?.let { actress ->
                        PagingSourceLoadResultPage(
                            prevKey = null, nextKey = actress.pageInfo.endCursor,
                            data = actress.edges?.mapNotNull { entity ->
                                entity.node?.let { node ->
                                    ActressEntity(
                                        id = node.id.toString(),
                                        photo = entity.node.photoLink ?: "",
                                        name = entity.node.name,
                                        isFavorite = false
                                    )
                                }
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

        override fun getRefreshKey(state: PagingState<String, ActressEntity>): String? = null

        companion object {

            /**
             * The GitHub REST API uses [1-based page numbering](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#pagination).
             */
            const val FIRST_PAGE_INDEX = 1
        }
    }
}