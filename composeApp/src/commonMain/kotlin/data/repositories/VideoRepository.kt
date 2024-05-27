package data.repositories

import androidx.paging.PagingData
import data.remote.VideoRemoteApi
import domain.model.Character
import domain.model.VideoDetailsEntity
import domain.model.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class VideoRepository(private val api: VideoRemoteApi) {
    fun searchByVideos(param: String): Flow<List<Character>> {
        return flow { emptyList<Character>() }
    }

    suspend fun getVideoDetails(videoId: String): Result<VideoDetailsEntity> {
        return withContext(Dispatchers.IO){api.loadDetails(videoId)}
    }

    suspend fun getRecentVideos(): Flow<PagingData<VideoEntity>> {
        return api.loadAllRecentVideos()
    }
}

