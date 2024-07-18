package domain.interactors.actresses

import androidx.paging.PagingData
import domain.interactors.type.BaseUseCaseFlow
import domain.model.ActressEntity
import domain.repositories.ActressRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SearchActressesListUsecase(
    private val repository: ActressRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCaseFlow<String, PagingData<ActressEntity>>(dispatcher) {
    override suspend fun build(param: String): Flow<PagingData<ActressEntity>> =
        repository.searchActresses(param)
}