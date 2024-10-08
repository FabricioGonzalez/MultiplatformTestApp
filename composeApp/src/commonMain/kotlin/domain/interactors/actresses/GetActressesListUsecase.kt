package domain.interactors.actresses

import androidx.paging.PagingData
import domain.interactors.type.BaseUseCaseFlow
import domain.model.ActressEntity
import domain.repositories.ActressRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetActressesListUsecase(
    private val repository: ActressRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCaseFlow<Unit, PagingData<ActressEntity>>(dispatcher) {
    override suspend fun build(param: Unit): Flow<PagingData<ActressEntity>> =
        repository.getAllActresses()
}