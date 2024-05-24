package data.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbPrefferedContent : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var label: String = ""
    var type: DbPrefferedContentType?
        get() {
            if (typeDescrition == null) return null

            return DbPrefferedContentType.valueOf(typeDescrition!!)
        }
        set(typeEnum) {
            typeDescrition = typeEnum?.name
        }
    private var typeDescrition: String? = null
}