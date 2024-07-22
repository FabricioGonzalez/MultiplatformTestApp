package data.repositories.realm

import androidx.paging.PagingData
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import data.remote.actresses.ActressRemoteApi
import domain.model.ActressEntity
import domain.model.inputs.TagFavoriteInput
import domain.repositories.ActressRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class RealmActressRepository(
    private val api: ActressRemoteApi,
    private val realmDb: Realm
) : ActressRepository {
    override suspend fun getActressDetails(actressId: String): Result<ActressEntity> {
        return withContext(Dispatchers.IO) { api.loadDetails(actressId) }
    }

    override suspend fun getAllActresses(): Flow<PagingData<ActressEntity>> {
        return api.loadAllActresses()
    }

    override suspend fun updateActress(actress: ActressEntity): ActressEntity? {
        return api.mutateActress(actress)
    }

    override suspend fun favoriteActress(input: TagFavoriteInput) {
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

    override suspend fun searchActresses(param: String): Flow<PagingData<ActressEntity>> {
        return api.searchActresses(searchText = param)
    }
}