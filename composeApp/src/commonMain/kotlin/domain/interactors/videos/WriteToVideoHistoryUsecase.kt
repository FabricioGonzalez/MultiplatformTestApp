package domain.interactors.videos

import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher

class WriteToVideoHistoryUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<String, Unit>(dispatcher) {
    override suspend fun block(param: String): Unit {
        repository.writeToHistory(param)
    }
}