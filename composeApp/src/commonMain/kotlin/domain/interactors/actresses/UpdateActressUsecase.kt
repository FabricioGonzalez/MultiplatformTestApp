package domain.interactors.actresses

import data.repositories.ActressRepository
import domain.interactors.type.BaseUseCase
import domain.model.ActressEntity
import kotlinx.coroutines.CoroutineDispatcher

class UpdateActressUsecase(
    private val repository: ActressRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<ActressEntity, Result<ActressEntity>>(dispatcher) {
    override suspend fun block(param: ActressEntity): Result<ActressEntity> {
        return repository.updateActress(param).let {
            if (it != null) Result.success(it)
            else Result.failure(Exception("Erro ao atualizar dados"))
        }
    }
}