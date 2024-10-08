package domain.interactors.actresses

import domain.interactors.type.BaseUseCase
import domain.model.ActressEntity
import domain.repositories.ActressRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetActressDetailsUsecase(
    private val repository: ActressRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<String, Result<ActressEntity>>(dispatcher) {
    override suspend fun block(param: String): Result<ActressEntity> {
        return repository.getActressDetails(param)
    }
}