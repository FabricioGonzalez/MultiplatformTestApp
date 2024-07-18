package domain.repositories

import androidx.paging.PagingData
import domain.model.ActressEntity
import domain.model.inputs.TagFavoriteInput
import kotlinx.coroutines.flow.Flow

interface ActressRepository {
    suspend fun getActressDetails(actressId: String): Result<ActressEntity>

    suspend fun getAllActresses(): Flow<PagingData<ActressEntity>>

    suspend fun updateActress(actress: ActressEntity): ActressEntity?
    suspend fun favoriteActress(input: TagFavoriteInput)
    suspend fun searchActresses(param: String): Flow<PagingData<ActressEntity>>
}