package data.room.paging_sources

/*private class LocalRecentVideosPagingSource(private val realmDb: Realm) : PagingSource<String, VideoEntity>() {
       override suspend fun load(params: PagingSourceLoadParams<String>): PagingSourceLoadResult<String, VideoEntity> {
           val page = params.key ?: FIRST_PAGE_INDEX


       }

       override fun getRefreshKey(state: PagingState<String, VideoEntity>): String? = null

       companion object {

           */
/**
 * The GitHub REST API uses [1-based page numbering](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#pagination).
 *//*
            const val FIRST_PAGE_INDEX = 1
        }
    }*/