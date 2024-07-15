package data.repositories

import androidx.paging.PagingData
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import data.entities.DbVideo
import data.entities.DbVideoHistory
import data.helpers.format
import data.helpers.now
import data.helpers.parse
import data.remote.VideoRemoteApi
import domain.model.HistoryEntry
import domain.model.PreferredContentEntity
import domain.model.VideoDetailsEntity
import domain.model.VideoEntity
import domain.model.enums.ContentPreferrence
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import multiplatform.realmDb

class VideoRepository(private val api: VideoRemoteApi) {
    fun searchByVideos(param: String): Flow<List<VideoEntity>> {
        return flow { emptyList<VideoEntity>() }
    }

    suspend fun getVideosByActress(param: String): Flow<PagingData<VideoEntity>> {
        return api.loadVideosByActress(param)
    }


    suspend fun writeToHistory(video: VideoDetailsEntity) {
        try {
            withContext(Dispatchers.IO) {
                val history = realmDb.query<DbVideoHistory>(
                    "videoId == $0",
                    video.id
                ).find().firstOrNull()
                if (history == null) {
                    println(realmDb.write {
                        copyToRealm(DbVideoHistory().apply {
                            this.videoId = video.id
                            this.watchedOn = LocalDateTime.now().format()
                            this.video = DbVideo().apply {
                                this.videoId = video.id
                                this.title = video.title
                                this.photo = video.photo
                            }
                        })
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun listHistory(): List<HistoryEntry> {
        try {
            return withContext(Dispatchers.IO) {
                realmDb.query<DbVideoHistory>().sort("watchedOn", Sort.DESCENDING)
                    .find().map { result ->
                        HistoryEntry(
                            id = result.videoId,
                            videoTitle = result.video?.title ?: "",
                            watchedOn = result.watchedOn.parse() ?: LocalDateTime.now(),
                            image = result.video?.photo
                        )
                    }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
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

    suspend fun getSearchedVideos(searchText: String): Flow<PagingData<VideoEntity>> {
        return api.loadSearchVideos(searchText)
    }
}

