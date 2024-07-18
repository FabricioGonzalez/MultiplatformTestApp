package domain.interactors.video_history

import domain.interactors.type.BaseUseCase
import domain.model.HistoryEntry
import domain.repositories.VideoRepository
import kotlinx.coroutines.CoroutineDispatcher

class ListHistoryUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<Unit, Result<List<HistoryEntry>>>(dispatcher) {
    override suspend fun block(param: Unit): Result<List<HistoryEntry>> {
        val results = repository.listHistory()

        return if (results.isNotEmpty()) {
            Result.success(results)
        } else Result.failure(Exception("Historico Vazio"))
    }
}