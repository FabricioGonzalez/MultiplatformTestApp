package data.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbVideoKey : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var videoId: String = ""
    var cursor: String = ""
    var page: Int = 0


}