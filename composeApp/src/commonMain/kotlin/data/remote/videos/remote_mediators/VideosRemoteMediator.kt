package data.remote.videos.remote_mediators

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import data.entities.DbVideo
import domain.model.VideoEntity
import graphql.VideosQuery
import io.realm.kotlin.Realm
import okio.IOException

@OptIn(ExperimentalPagingApi::class)
internal class VideosRemoteMediator(private val apolloClient: ApolloClient, private val realmDb: Realm) :
    RemoteMediator<String, VideoEntity>() {
    override suspend fun initialize(): InitializeAction {/*val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)*/

        return if (false) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<String, VideoEntity>
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

                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )

                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.

                    lastItem.cursor
                }
            }

            // Suspending network load via Retrofit. This doesn't need to be
            // wrapped in a withContext(Dispatcher.IO) { ... } block since
            // Retrofit's Coroutine CallAdapter dispatches on a worker
            // thread.
            var hasMorePages = true

            val result = apolloClient.query(
                VideosQuery(
                    cursorStart = Optional.present(loadKey), afterSize = Optional.present(40)
                )
            ).execute().let {
                when {
                    !it.hasErrors() -> {
                        it.dataOrThrow().videos?.let { videos ->
                            realmDb.write {
                                hasMorePages = videos.pageInfo.hasNextPage

                                videos.edges?.map { entity ->
                                    if (loadType == LoadType.REFRESH) {

                                    }
                                    entity.node?.let { node ->
                                        copyToRealm(DbVideo().apply {
                                            videoId = node.id.toString()
                                            title = node.title
                                            photo = node.photoLink
                                            createdAt = node.createdAt.toString()
                                            updatedAt = node.updatedAt.toString()
                                            originalCreationDate = node.originalCreationDate.toString()
                                        })
                                    }
                                }
                            } ?: emptyList()
                        }
                    }

                    it.hasErrors() -> {
                        emptyList()
                    }

                    else -> {
                        emptyList()
                    }
                }
            } ?: emptyList()

            /*VideoEntity(
                cursor = entity.cursor,
                photo = entity.node?.photoLink ?: "",
                title = entity.node?.title ?: "",
                id = entity.node?.id.toString()
            )*/

            /*database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    userDao.deleteByQuery(query)
                }

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                userDao.insertAll(response.users)
            }*/

            MediatorResult.Success(
                endOfPaginationReached = !hasMorePages
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        }/* catch (e: HttpException) {
                MediatorResult.Error(e)
            }*/
    }

}
