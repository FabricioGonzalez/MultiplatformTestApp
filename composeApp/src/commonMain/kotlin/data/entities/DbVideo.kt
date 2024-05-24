package data.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbVideo : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var title: String = ""
    var photo: String? = null
    var link: String = ""

    /*val actresses: RealmResults<DbActress> by backlinks(DbActress::videos)*/
    /*val tags: RealmResults<DbTag> by backlinks(DbTag::videos)*/
}