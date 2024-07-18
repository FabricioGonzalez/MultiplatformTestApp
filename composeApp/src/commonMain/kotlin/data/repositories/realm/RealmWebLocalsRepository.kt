package data.repositories.realm

import data.entities.DbWebLocal
import domain.model.SiteEntity
import domain.repositories.WebLocalsRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class RealmWebLocalsRepository(private val realmDb: Realm) : WebLocalsRepository {
    override suspend fun loadAllLocals(): List<SiteEntity> {
        try {
            return withContext(Dispatchers.IO) {
                realmDb.query<DbWebLocal>().find().map {
                    SiteEntity(
                        id = it._id.toHexString(),
                        name = it.name,
                        url = it.url,
                    )
                }
            }
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }

    override suspend fun modifyWebLocal(siteEntity: SiteEntity): SiteEntity {
        return realmDb.write {
            copyToRealm(DbWebLocal().apply {
                if (siteEntity.id != null) {
                    _id = BsonObjectId(siteEntity.id)
                }
                name = siteEntity.name
                url = siteEntity.url
            })
        }.let {
            SiteEntity(id = it._id.toString(), name = it.name, url = it.url)
        }
    }

    override suspend fun deleteWebLocal(localId: String) {
        realmDb.write {
            // Get the live frog object with findLatest(), then delete it
            realmDb.query<DbWebLocal>("_id == $0", BsonObjectId(localId)).first().find()
                ?.let { item ->
                    findLatest(item)
                        ?.also { delete(it) }
                }
        }
    }


    override suspend fun loadWebLocal(localId: String): SiteEntity? {
        try {
            val result = realmDb.query<DbWebLocal>("_id == $0", ObjectId(localId)).find().map {
                SiteEntity(
                    id = it._id.toString(),
                    name = it.name,
                    url = it.url,
                )
            }.firstOrNull()

            return result
        } catch (e: Exception) {
            println(e)
            return null
        }
    }
}