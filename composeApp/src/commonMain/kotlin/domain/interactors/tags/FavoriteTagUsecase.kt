package domain.interactors.tags

import data.repositories.TagRepository
import domain.interactors.type.BaseUseCase
import domain.model.inputs.TagFavoriteInput
import kotlinx.coroutines.CoroutineDispatcher

class FavoriteTagUsecase(
    private val repository: TagRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<TagFavoriteInput, Result<Unit>>(dispatcher) {
    override suspend fun block(param: TagFavoriteInput): Result<Unit> {
        repository.favoriteTag(param)
        return Result.success(Unit)
    }
}

