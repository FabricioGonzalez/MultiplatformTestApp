package domain.interactors.videos

import androidx.paging.PagingData
import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCase
import domain.model.PreferredContentEntity
import domain.model.VideoEntity
import domain.model.enums.ContentPreferrence
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetAllRecentVideosUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<PreferredContentEntity, Result<Flow<PagingData<VideoEntity>>>>(dispatcher) {
    override suspend fun block(param: PreferredContentEntity): Result<Flow<PagingData<VideoEntity>>> =
        try {
            when (param.type) {
                ContentPreferrence.Actress -> Result.success(repository.getVideosByActress(param.title))
                ContentPreferrence.Tag -> Result.success(repository.getVideosByTag(param.title))
                ContentPreferrence.RecentContent -> Result.success(repository.getRecentVideos())
                ContentPreferrence.MostLiked -> TODO()
            }
        } catch (
            e: Exception
        ) {
            Result.failure(e)
        }
}