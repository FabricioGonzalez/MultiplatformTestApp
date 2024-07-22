package data.remote.actresses.paging_sources

import app.cash.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import domain.model.ActressEntity
import graphql.SearchActressesByNameQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


internal class SearchActressesPagingSource(
    private val apolloClient: ApolloClient, private val searchText: String
) : PagingSource<String, ActressEntity>() {
    override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, ActressEntity> {
        val page = params.key ?: FIRST_PAGE_INDEX

        val result = withContext(Dispatchers.IO) {
            apolloClient.query(
                when (params) {
                    is PagingSourceLoadParamsRefresh<String> -> {
                        SearchActressesByNameQuery(
                            afterSize = Optional.present(40), actressName = searchText
                        )
                    }

                    is PagingSourceLoadParamsAppend<String> -> {
                        SearchActressesByNameQuery(
                            afterSize = Optional.present(40),
                            cursorEnd = Optional.present(params.key),
                            actressName = searchText
                        )
                    }

                    is PagingSourceLoadParamsPrepend<String> -> {
                        SearchActressesByNameQuery(
                            beforeSize = Optional.present(40),
                            cursorStart = Optional.present(params.key),
                            actressName = searchText
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
