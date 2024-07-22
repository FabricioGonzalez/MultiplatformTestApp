package domain.interactors.videos

import data.remote.videos.dtos.VideoAddedInfo
import domain.interactors.type.BaseUseCaseFlow
import domain.repositories.VideoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class GetNewlyAddedVideosUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCaseFlow<Unit, VideoAddedInfo?>(dispatcher) {
    override suspend fun build(param: Unit): Flow<VideoAddedInfo?> {
        return repository.onVideoAdded().transform { emit(it.fold({ su -> su }, { err -> null })) }
    }
}