package domain.interactors.web_locals

import domain.interactors.type.BaseUseCase
import domain.model.SiteEntity
import domain.repositories.WebLocalsRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetWebLocalsListUsecase(
    private val repository: WebLocalsRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<Unit, List<SiteEntity>>(dispatcher) {
    override suspend fun block(param: Unit): List<SiteEntity> = repository.loadAllLocals()
}