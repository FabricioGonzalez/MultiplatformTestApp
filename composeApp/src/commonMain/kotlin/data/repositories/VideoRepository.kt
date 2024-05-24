package data.repositories

import androidx.paging.PagingData
import data.remote.VideoRemoteApi
import domain.model.Character
import domain.model.VideoEntity
import graphql.type.VideoDBEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VideoRepository(private val api: VideoRemoteApi) {
    fun searchByVideos(param: String): Flow<List<Character>> {
        return flow { emptyList<Character>() }
    }

    fun getVideoDetails(videoId: String): VideoDBEntity {
        return VideoDBEntity()
    }

    suspend fun getRecentVideos(): Flow<PagingData<VideoEntity>> {
        return api.loadAllRecentVideos()
    }
}

