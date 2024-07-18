package domain.interactors.actresses

import domain.interactors.type.BaseUseCase
import domain.model.inputs.TagFavoriteInput
import domain.repositories.ActressRepository
import kotlinx.coroutines.CoroutineDispatcher

class FavoriteActressUsecase(
    private val repository: ActressRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<TagFavoriteInput, Result<Unit>>(dispatcher) {
    override suspend fun block(param: TagFavoriteInput): Result<Unit> {
        repository.favoriteActress(param)
        return Result.success(Unit)
    }
}

