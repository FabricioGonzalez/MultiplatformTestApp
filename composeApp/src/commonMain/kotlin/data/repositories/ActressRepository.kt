package data.repositories

import androidx.paging.PagingData
import multiplatform.realmDb
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import data.remote.ActressRemoteApi
import domain.model.ActressEntity
import domain.model.inputs.TagFavoriteInput
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class ActressRepository(private val api: ActressRemoteApi) {
    suspend fun getActressDetails(actressId: String): Result<ActressEntity> {
        return withContext(Dispatchers.IO) { api.loadDetails(actressId) }
    }

    suspend fun getAllActresses(): Flow<PagingData<ActressEntity>> {
        return api.loadAllActresses()
    }

    suspend fun updateActress(actress: ActressEntity): ActressEntity? {
        return api.mutateActress(actress)
    }

    suspend fun favoriteActress(input: TagFavoriteInput) {
        try {
            withContext(Dispatchers.IO) {
                val actress = realmDb.query<DbPreferredContent>(
                    "label == $0 and typeDescrition == $1",
                    input.name,
                    DbPreferredContentType.ActressContent.name
                ).find().firstOrNull()
                if (actress == null) {
                    println(realmDb.write {
                        copyToRealm(DbPreferredContent().apply {
                            label = input.name
                            type = DbPreferredContentType.ActressContent
                        })
                    })
                } else {
                    realmDb.writeBlocking {
                        // Get the live frog object with findLatest(), then delete it
                        findLatest(actress)
                            ?.also { delete(it) }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun searchActresses(param: String): Flow<PagingData<ActressEntity>> {
        return api.searchActresses(searchText = param)
    }
}