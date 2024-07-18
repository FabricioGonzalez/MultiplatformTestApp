package domain.interactors.web_locals

import domain.interactors.type.BaseUseCase
import domain.repositories.WebLocalsRepository
import kotlinx.coroutines.CoroutineDispatcher

class DeleteWebLocalUsecase(
    private val repository: WebLocalsRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<String, Unit>(dispatcher) {
    override suspend fun block(param: String): Unit = try {
        repository.deleteWebLocal(param)
    } catch (e: Exception) {
        throw e
    }
}