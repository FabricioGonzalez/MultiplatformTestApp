package data.entities

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbVideo : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var videoId: String = ""
    var title: String = ""
    var photo: String? = null
    var createdAt: String = ""
    var updatedAt: String = ""
    var originalCreationDate: String = ""

    var actresses: RealmList<DbActress> = realmListOf()
    var tags: RealmList<DbTag> = realmListOf()
}