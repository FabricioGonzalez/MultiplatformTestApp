package domain.interactors.videos

import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCase
import domain.model.VideoDetailsEntity
import kotlinx.coroutines.CoroutineDispatcher

class WriteToVideoHistoryUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<VideoDetailsEntity, Unit>(dispatcher) {
    override suspend fun block(param: VideoDetailsEntity): Unit {
        repository.writeToHistory(param)
    }
}