package domain.interactors.videos

import androidx.paging.PagingData
import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCaseFlow
import domain.model.VideoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetVideosByTagUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCaseFlow<String, PagingData<VideoEntity>>(dispatcher) {
    override suspend fun build(param: String): Flow<PagingData<VideoEntity>> = repository.getVideosByTag(param)
}