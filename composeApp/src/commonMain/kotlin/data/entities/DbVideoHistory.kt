package data.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class DbVideoHistory : RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var videoId: String = ""
    var watchedOn: String = ""
    var video: DbVideo? = null

}