package data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadParamsAppend
import app.cash.paging.PagingSourceLoadParamsPrepend
import app.cash.paging.PagingSourceLoadParamsRefresh
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import multiplatform.realmDb
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import domain.model.ActressEntity
import domain.model.PlayerEntity
import domain.model.TagEntity
import domain.model.VideoDetailsEntity
import domain.model.VideoEntity
import graphql.SearchVideosByTermQuery
import graphql.VideoDetailsQuery
import graphql.VideosByActressNameQuery
import graphql.VideosByTagQuery
import graphql.VideosQuery
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
                                createdAt = Instant.parse(video.createdAt.toString())
                                    .toLocalDateTime(
                                        TimeZone.currentSystemDefault()
                                    ),
                                addedToAt = Instant.parse(video.originalCreationDate.toString())
                                    .toLocalDateTime(
                                        TimeZone.currentSystemDefault()
                                    ),
                                actresses = video.actresses?.mapNotNull { actress ->
                                    actress?.let { act ->
                                        ActressEntity(
                                            id = act.id.toString(),
                                            name = act.name,
                                            photo = act.photoLink ?: "",
                                            isFavorite = realmDb.query<DbPreferredContent>(
                                                "label == $0 and typeDescrition == $1",
                                                act.name,
                                                DbPreferredContentType.ActressContent.name
                                            ).first().find() != null,
                                            link = act.url ?: ""
                                        )
                                    }
                                } ?: emptyList(),
                                tags = video.tags?.mapNotNull { tag ->
                                    tag?.let { t ->
                                        TagEntity(
                                            id = t.id.toString(),
                                            name = t.name,
                                            isFavorite = realmDb.query<DbPreferredContent>(
                                                "label == $0 and typeDescrition == $1",
                                                t.name,
                                                DbPreferredContentType.TagContent.name
                                            ).first().find() != null
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

    suspend fun loadVideosByTag(tagName: String): Flow<PagingData<VideoEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(pagingConfig) {
                VideosByTagPagingSource(apolloClient, tagName = tagName)
            }.flow
        }
    }

    suspend fun loadVideosByActress(actressName: String): Flow<PagingData<VideoEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(pagingConfig) {
                VideosByActressPagingSource(apolloClient, actressName = actressName)
            }.flow
        }
    }

    suspend fun loadFromGraphQl(loadKey: PagingSourceLoadParams<String>): VideoApiResult? {
        val result = withContext(Dispatchers.IO) {
            apolloClient.query(
                when (loadKey) {
                    is PagingSourceLoadParamsRefresh<String> -> {
                        VideosQuery(afterSize = Optional.present(40))
                    }

                    is PagingSourceLoadParamsAppend<String> -> {
                        VideosQuery(
                            afterSize = Optional.present(40),
                            cursorEnd = Optional.present(loadKey.key)
                        )
                    }

                    is PagingSourceLoadParamsPrepend<String> -> {
                        VideosQuery(
                            beforeSize = Optional.present(40),
                            cursorStart = Optional.present(loadKey.key)
                        )
                    }

                }
            ).execute()
        }
        return when {
            !result.hasErrors() -> {
                result.dataOrThrow().videos?.let { videos ->
                    VideoApiResult(
                        startCursor = videos.pageInfo.startCursor,
                        endCursor = videos.pageInfo.endCursor,
                        videos = videos.edges?.map { entity ->
                            VideoEntity(
                                cursor = entity.cursor,
                                photo = entity.node?.photoLink ?: "",
                                title = entity.node?.title ?: "",
                                id = entity.node?.id.toString()
                            )
                        } ?: emptyList()
                    )
                }
            }

            result.hasErrors() -> {
                null
            }

            else -> {
                null
            }
        }

    }

    @OptIn(ExperimentalPagingApi::class)
    suspend fun loadAllRecentVideos(): Flow<PagingData<VideoEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(
                config = pagingConfig,
                /*remoteMediator = VideosRemoteMediator(apolloClient),*/
                pagingSourceFactory = {
                    RemoteRecentVideosPagingSource(apolloClient)
                })
                /*RecentVideosPagingSource(apolloClient)*/.flow
        }
    }

    suspend fun loadSearchVideos(searchText: String): Flow<PagingData<VideoEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(
                config = pagingConfig,
                /*remoteMediator = VideosRemoteMediator(apolloClient),*/
                pagingSourceFactory = {
                    RemoteSearchVideosPagingSource(apolloClient, searchText)
                })
                /*RecentVideosPagingSource(apolloClient)*/.flow
        }
    }

    private class LocalRecentVideosPagingSource : PagingSource<String, VideoEntity>() {
        override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, VideoEntity> {
            val page = params.key ?: FIRST_PAGE_INDEX

            /* val result = withContext(Dispatchers.IO) {
                 apolloClient.query(
                     when (params) {
                         is PagingSourceLoadParamsRefresh<String> -> {
                             VideosQuery(afterSize = Optional.present(40))
                         }

                         is PagingSourceLoadParamsAppend<String> -> {
                             VideosQuery(
                                 afterSize = Optional.present(40),
                                 cursorEnd = Optional.present(params.key)
                             )
                         }

                         is PagingSourceLoadParamsPrepend<String> -> {
                             VideosQuery(
                                 beforeSize = Optional.present(40),
                                 cursorStart = Optional.present(params.key)
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
                 }*/
            return PagingSourceLoadResultPage(
                data = emptyList<VideoEntity>(),
                nextKey = null,
                prevKey = null
            )
        }

        override fun getRefreshKey(state: PagingState<String, VideoEntity>): String? = null

        companion object {

            /**
             * The GitHub REST API uses [1-based page numbering](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#pagination).
             */
            const val FIRST_PAGE_INDEX = 1
        }
    }

    private class RemoteSearchVideosPagingSource(
        private val apolloClient: ApolloClient,
        private val search: String
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

    private class RemoteRecentVideosPagingSource(
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
                                afterSize = Optional.present(40),
                                cursorEnd = Optional.present(params.key)
                            )
                        }

                        is PagingSourceLoadParamsPrepend<String> -> {
                            VideosQuery(
                                beforeSize = Optional.present(40),
                                cursorStart = Optional.present(params.key)
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


    /*    @OptIn(ExperimentalPagingApi::class)
        private class VideosRemoteMediator(private val apolloClient: ApolloClient) :
            RemoteMediator<String, VideoEntity>() {
            override suspend fun load(
                loadType: LoadType,
                state: androidx.paging.PagingState<String, VideoEntity>
            ): MediatorResult {
                return try {
                    // The network load method takes an optional after=<user.id>
                    // parameter. For every page after the first, pass the last user
                    // ID to let it continue from where it left off. For REFRESH,
                    // pass null to load the first page.
                    val loadKey = when (loadType) {
                        LoadType.REFRESH -> null
                        // In this example, you never need to prepend, since REFRESH
                        // will always load the first page in the list. Immediately
                        // return, reporting end of pagination.
                        LoadType.PREPEND ->
                            return MediatorResult.Success(endOfPaginationReached = true)

                        LoadType.APPEND -> {
                            val lastItem = state.lastItemOrNull()
                                ?: return MediatorResult.Success(
                                    endOfPaginationReached = true
                                )

                            // You must explicitly check if the last item is null when
                            // appending, since passing null to networkService is only
                            // valid for initial load. If lastItem is null it means no
                            // items were loaded after the initial REFRESH and there are
                            // no more items to load.

                            lastItem.id
                        }
                    }

                    // Suspending network load via Retrofit. This doesn't need to be
                    // wrapped in a withContext(Dispatcher.IO) { ... } block since
                    // Retrofit's Coroutine CallAdapter dispatches on a worker
                    // thread.
                  this.load
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            userDao.deleteByQuery(query)
                        }

                        // Insert new users into database, which invalidates the
                        // current PagingData, allowing Paging to present the updates
                        // in the DB.
                        userDao.insertAll(response.users)
                    }

                    MediatorResult.Success(
                        endOfPaginationReached = response.nextKey == null
                    )
                } catch (e: IOException) {
                    MediatorResult.Error(e)
                } catch (e: HttpException) {
                    MediatorResult.Error(e)
                }
            }

        }*/

    private class VideosByTagPagingSource(
        private val apolloClient: ApolloClient,
        private val tagName: String
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

    private class VideosByActressPagingSource(
        private val apolloClient: ApolloClient,
        private val actressName: String
    ) : PagingSource<String, VideoEntity>() {
        override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, VideoEntity> {
            val page = params.key ?: FIRST_PAGE_INDEX

            val result = withContext(Dispatchers.IO) {
                apolloClient.query(
                    when (params) {
                        is PagingSourceLoadParamsRefresh<String> -> {
                            VideosByActressNameQuery(
                                afterSize = Optional.present(40),
                                name = actressName
                            )
                        }

                        is PagingSourceLoadParamsAppend<String> -> {
                            VideosByActressNameQuery(
                                afterSize = Optional.present(40),
                                cursorEnd = Optional.present(params.key),
                                name = actressName
                            )
                        }

                        is PagingSourceLoadParamsPrepend<String> -> {
                            VideosByActressNameQuery(
                                beforeSize = Optional.present(40),
                                cursorStart = Optional.present(params.key),
                                name = actressName
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
}

data class VideoApiResult(
    val startCursor: String?,
    val endCursor: String?,
    val videos: List<VideoEntity>
)