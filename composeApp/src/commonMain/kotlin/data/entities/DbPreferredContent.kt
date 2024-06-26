package data.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class DbPreferredContent : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var label: String = ""
    var type: DbPreferredContentType?
        get() {
            if (typeDescrition == null) return null

            return DbPreferredContentType.valueOf(typeDescrition!!)
        }
        set(typeEnum) {
            typeDescrition = typeEnum?.name
        }
    private var typeDescrition: String? = null
}