package domain.interactors.web_locals

import domain.interactors.type.BaseUseCase
import domain.model.SiteEntity
import domain.repositories.WebLocalsRepository
import kotlinx.coroutines.CoroutineDispatcher

class ModifyWebLocalUsecase(
    private val repository: WebLocalsRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<SiteEntity, SiteEntity?>(dispatcher) {
    override suspend fun block(param: SiteEntity): SiteEntity? = try {
        repository.modifyWebLocal(param)
    } catch (e: Exception) {
        null
    }
}