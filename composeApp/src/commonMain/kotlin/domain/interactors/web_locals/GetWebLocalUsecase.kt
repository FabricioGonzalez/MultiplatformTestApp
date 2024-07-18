package domain.interactors.web_locals

import domain.interactors.type.BaseUseCase
import domain.model.SiteEntity
import domain.repositories.WebLocalsRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetWebLocalUsecase(
    private val repository: WebLocalsRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<String, SiteEntity?>(dispatcher) {
    override suspend fun block(param: String): SiteEntity? = repository.loadWebLocal(param)
}