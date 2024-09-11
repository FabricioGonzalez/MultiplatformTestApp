package data.remote.videos

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadParamsAppend
import app.cash.paging.PagingSourceLoadParamsPrepend
import app.cash.paging.PagingSourceLoadParamsRefresh
import com.apollographql.apollo3.api.Optional
import data.ApolloConnector
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import data.remote.videos.dtos.VideoAddedInfo
import data.remote.videos.dtos.VideoApiResult
import data.remote.videos.paging_sources.RemoteRecentVideosPagingSource
import data.remote.videos.paging_sources.RemoteSearchVideosPagingSource
import data.remote.videos.paging_sources.VideosByActressPagingSource
import data.remote.videos.paging_sources.VideosByTagPagingSource
import domain.model.ActressEntity
import domain.model.PlayerEntity
import domain.model.TagEntity
import domain.model.VideoDetailsEntity
import domain.model.VideoEntity
import graphql.OnVideoAddedSubscription
import graphql.VideoDetailsQuery
import graphql.VideosQuery
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class VideoRemoteApi(private val apolloClient: ApolloConnector, private val realmDb: Realm) {

    suspend fun onVideoAdded(): Flow<Result<VideoAddedInfo>> =
        withContext(Dispatchers.IO) {
            apolloClient.subscribe(
                OnVideoAddedSubscription()
            )
                .catch {
                    println(it)
                }
                .transform { response ->
                    val data = response.data
                    if (data != null) {
                        data.videoAdded?.let {
                            emit(
                                Result.success(
                                    VideoAddedInfo(
                                        id = it.id.toString(),
                                        title = it.title,
                                        photoLink = it.photo,
                                        quantity = it.quantity
                                    )
                                )
                            )
                        }
                    } else {
                        // Something wrong happened
                        if (response.exception != null) {
                            response.exception?.cause?.let { err -> emit(Result.failure(err)) }
                        } else {
                            emit(Result.failure(Error(response.errors?.map { it.message }
                                ?.reduce { acc, error -> "$acc, $error" })))
                        }
                    }
                }
        }

    suspend fun loadDetails(id: String): Result<VideoDetailsEntity> {
        return withContext(Dispatchers.IO) {

            val result = apolloClient.query(VideoDetailsQuery(id))

            when {
                !result.hasErrors() -> {
                    result.dataOrThrow().videos?.edges?.firstOrNull()?.node?.let { video ->
                        Result.success(VideoDetailsEntity(
                            id = video.id.toString(),
                            title = video.title,
                            photo = video.photoLink ?: "",
                            createdAt = Instant.parse(video.createdAt.toString()).toLocalDateTime(
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
                        ))
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
            )
        }
        return when {
            !result.hasErrors() -> {
                result.dataOrThrow().videos?.let { videos ->
                    VideoApiResult(startCursor = videos.pageInfo.startCursor,
                        endCursor = videos.pageInfo.endCursor,
                        videos = videos.edges?.map { entity ->
                            VideoEntity(
                                cursor = entity.cursor,
                                photo = entity.node?.photoLink ?: "",
                                title = entity.node?.title ?: "",
                                id = entity.node?.id.toString()
                            )
                        } ?: emptyList())
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

    suspend fun loadAllRecentVideos(): Flow<PagingData<VideoEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(config = pagingConfig,/*remoteMediator = VideosRemoteMediator(apolloClient, realmDb),*/
                pagingSourceFactory = {
                    RemoteRecentVideosPagingSource(apolloClient)
                })/*RecentVideosPagingSource(apolloClient)*/.flow
        }
    }

    suspend fun loadSearchVideos(searchText: String): Flow<PagingData<VideoEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(config = pagingConfig,/*remoteMediator = VideosRemoteMediator(apolloClient,),*/
                pagingSourceFactory = {
                    RemoteSearchVideosPagingSource(apolloClient, searchText)
                }).flow
        }
    }

}