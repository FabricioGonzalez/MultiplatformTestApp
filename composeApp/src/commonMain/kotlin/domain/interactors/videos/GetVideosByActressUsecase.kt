package domain.interactors.videos

import androidx.paging.PagingData
import domain.interactors.type.BaseUseCaseFlow
import domain.model.VideoEntity
import domain.repositories.VideoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetVideosByActressUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCaseFlow<String, PagingData<VideoEntity>>(dispatcher) {
    override suspend fun build(param: String): Flow<PagingData<VideoEntity>> =
        repository.getVideosByActress(param)
}