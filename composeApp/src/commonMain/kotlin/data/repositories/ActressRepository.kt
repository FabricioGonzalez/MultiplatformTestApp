package data.repositories

import androidx.paging.PagingData
import data.remote.ActressRemoteApi
import domain.model.ActressEntity
import kotlinx.coroutines.flow.Flow


class ActressRepository(private val api: ActressRemoteApi) {
    /*fun searchByVideos(param: String): Flow<List<Character>> {
        return flow { emptyList<Character>() }
    }*/

   /* suspend fun getVideoDetails(videoId: String): Result<VideoDetailsEntity> {
        return withContext(Dispatchers.IO){api.loadDetails(videoId)}
    }*/

    suspend fun getAllActresses(): Flow<PagingData<ActressEntity>> {
        return api.loadAllActresses()
    }
}