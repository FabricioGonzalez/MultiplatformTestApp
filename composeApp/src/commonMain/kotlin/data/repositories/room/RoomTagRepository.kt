package data.repositories.room

import data.room.DatabaseSpecs
import domain.model.inputs.TagFavoriteInput
import domain.repositories.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class RoomTagRepository(private val roomDb: DatabaseSpecs) : TagRepository {
    override suspend fun favoriteTag(input: TagFavoriteInput) {
        try {
            withContext(Dispatchers.IO) {
                /* val tag = realmDb.query<DbPreferredContent>(
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
                         findLatest(tag)
                             ?.also { delete(it) }
                     }
                 }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}