package domain.interactors.videos

import androidx.paging.PagingData
import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCaseFlow
import domain.model.VideoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllRecentVideosUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCaseFlow<Unit, PagingData<VideoEntity>>(dispatcher) {
    override suspend fun build(param: Unit): Flow<PagingData<VideoEntity>> = flow { repository.getRecentVideos() }
}