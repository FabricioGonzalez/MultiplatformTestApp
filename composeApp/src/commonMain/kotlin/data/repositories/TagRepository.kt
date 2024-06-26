package data.repositories

import data.data_access.realmDb
import data.entities.DbPreferredContent
import data.entities.DbPreferredContentType
import domain.model.inputs.TagFavoriteInput
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class TagRepository {
    suspend fun favoriteTag(input: TagFavoriteInput) {
        try {
            withContext(Dispatchers.IO) {
                val tag = realmDb.query<DbPreferredContent>(
                    "label == $0 and typeDescrition == $1",
                    input.name,
                    DbPreferredContentType.TagContent.name
                ).find().firstOrNull()
                if (tag == null) {
                    println(realmDb.write {
                        copyToRealm(DbPreferredContent().apply {
                            label = input.name
                            type = DbPreferredContentType.TagContent
                        })
                    })
                } else {
                    realmDb.writeBlocking {
                        // Get the live frog object with findLatest(), then delete it
                        if (tag != null) {
                            findLatest(tag)
                                ?.also { delete(it) }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}