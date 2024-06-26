package data.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbActress : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var photoLink: String = ""
    /*val videos: RealmResults<DbVideo> by backlinks(DbVideo::actresses)*/
}
