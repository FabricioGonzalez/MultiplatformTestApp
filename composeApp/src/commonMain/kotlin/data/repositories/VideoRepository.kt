package data.repositories

import androidx.paging.PagingData
import data.data_access.realmDb
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import data.entities.DbVideoHistory
import data.remote.VideoRemoteApi
import domain.model.PreferredContentEntity
import domain.model.VideoDetailsEntity
import domain.model.VideoEntity
import domain.model.enums.ContentPreferrence
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class VideoRepository(private val api: VideoRemoteApi) {
    fun searchByVideos(param: String): Flow<List<VideoEntity>> {
        return flow { emptyList<VideoEntity>() }
    }

    suspend fun getVideosByActress(param: String): Flow<PagingData<VideoEntity>> {
        return api.loadVideosByActress(param)
    }


    suspend fun writeToHistory(videoId: String) {
        try {
            withContext(Dispatchers.IO) {
                val tag = realmDb.query<DbVideoHistory>(
                    "videoId == $0",
                    videoId
                ).find().firstOrNull()
                if (tag == null) {
                    println(realmDb.write {
                        copyToRealm(DbVideoHistory().apply {
                            this.videoId = videoId
                        })
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun listHistory() {

    }

    suspend fun getContentPreference(): List<PreferredContentEntity> {
        return realmDb.query<DbPreferredContent>()
            .find()
            .map {
                PreferredContentEntity(
                    title = it.label,
                    type = when (it.type!!) {
                        DbPreferredContentType.ActressContent -> ContentPreferrence.Actress
                        DbPreferredContentType.TagContent -> ContentPreferrence.Tag
                        DbPreferredContentType.RecentContent -> ContentPreferrence.RecentContent
                        DbPreferredContentType.MostLikedContent -> ContentPreferrence.MostLiked
                    }
                )
            }
    }


    suspend fun getVideosByTag(param: String): Flow<PagingData<VideoEntity>> {
        return api.loadVideosByTag(param)
    }

    suspend fun getVideoDetails(videoId: String): Result<VideoDetailsEntity> {
        return try {
            withContext(Dispatchers.IO) { api.loadDetails(videoId) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentVideos(): Flow<PagingData<VideoEntity>> {
        return api.loadAllRecentVideos()
    }
}

