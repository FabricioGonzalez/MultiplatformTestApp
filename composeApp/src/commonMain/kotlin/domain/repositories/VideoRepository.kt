package domain.repositories

import androidx.paging.PagingData
import domain.model.HistoryEntry
import domain.model.PreferredContentEntity
import domain.model.VideoDetailsEntity
import domain.model.VideoEntity
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    suspend fun searchByVideos(param: String): Flow<List<VideoEntity>>

    suspend fun getVideosByActress(param: String): Flow<PagingData<VideoEntity>>
    suspend fun writeToHistory(video: VideoDetailsEntity)
    suspend fun listHistory(): List<HistoryEntry>
    suspend fun getContentPreference(): List<PreferredContentEntity>
    suspend fun getVideosByTag(param: String): Flow<PagingData<VideoEntity>>
    suspend fun getVideoDetails(videoId: String): Result<VideoDetailsEntity>
    suspend fun getRecentVideos(): Flow<PagingData<VideoEntity>>
    suspend fun getSearchedVideos(searchText: String): Flow<PagingData<VideoEntity>>
}