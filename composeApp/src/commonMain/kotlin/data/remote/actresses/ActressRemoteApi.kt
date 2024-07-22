package data.remote.actresses

import app.cash.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import data.remote.actresses.paging_sources.SearchActressesPagingSource
import domain.model.ActressEntity
import graphql.ActressByIdQuery
import graphql.ActressesQuery
import graphql.UpdateActressMutation
import graphql.type.MutateActressInput
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class ActressRemoteApi(private val apolloClient: ApolloClient, private val realmDb: Realm) {

    suspend fun loadDetails(id: String): Result<ActressEntity> {
        return withContext(Dispatchers.IO) {

            val result = apolloClient.query(ActressByIdQuery(id)).execute()

            when {
                !result.hasErrors() -> {
                    result.dataOrThrow().actresses?.edges?.firstOrNull()?.node?.let { actress ->
                        Result.success(
                            ActressEntity(
                                id = actress.id.toString(),
                                name = actress.name,
                                photo = actress.photoLink ?: "",
                                link = actress.url ?: "",
                                isFavorite = realmDb.query<DbPreferredContent>(
                                    "label == $0 and typeDescrition == $1",
                                    actress.name,
                                    DbPreferredContentType.ActressContent.name
                                ).first().find() != null
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

    suspend fun loadAllActresses(): Flow<PagingData<ActressEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(pagingConfig) {
                ActressesPagingSource(apolloClient)
            }.flow
        }
    }

    suspend fun mutateActress(actress: ActressEntity): ActressEntity? {
        try {
            withContext(Dispatchers.IO) {
                apolloClient.mutation(
                    UpdateActressMutation(
                        MutateActressInput(
                            id = actress.id,
                            name = actress.name,
                            photoLink = actress.photo
                        )
                    )
                ).execute()
            }
            return actress
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun searchActresses(searchText: String): Flow<PagingData<ActressEntity>> {
        return withContext(Dispatchers.IO) {
            val pagingConfig = PagingConfig(pageSize = 40, initialLoadSize = 40)

            Pager(pagingConfig) {
                SearchActressesPagingSource(apolloClient, searchText)
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
                            ActressesQuery(
                                afterSize = Optional.present(40),
                                cursorEnd = Optional.present(params.key)
                            )
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
                                        isFavorite = false,
                                        link = entity.node.url ?: ""
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