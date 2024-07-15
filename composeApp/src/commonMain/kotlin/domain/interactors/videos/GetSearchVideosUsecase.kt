package domain.interactors.videos

import androidx.paging.PagingData
import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCase
import domain.model.VideoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetSearchVideosUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<String, Result<Flow<PagingData<VideoEntity>>>>(dispatcher) {
    override suspend fun block(param: String): Result<Flow<PagingData<VideoEntity>>> =
        try {
            Result.success(repository.getSearchedVideos(param))

        } catch (
            e: Exception
        ) {
            Result.failure(e)
        }
}