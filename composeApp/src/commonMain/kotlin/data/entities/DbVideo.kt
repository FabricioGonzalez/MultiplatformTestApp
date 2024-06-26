package data.entities

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbVideo : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var title: String = ""
    var photo: String? = null
    var link: String = ""

    var actresses: RealmList<DbActress> = realmListOf()
    var tags: RealmList<DbTag> = realmListOf()
}