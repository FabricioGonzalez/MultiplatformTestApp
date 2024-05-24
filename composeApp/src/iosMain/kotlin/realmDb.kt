package data.data_access

import data.entities.DbActress
import data.entities.DbPrefferedContent
import data.entities.DbTag
import data.entities.DbVideo
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

actual val realmDb = Realm.open(
    configuration = RealmConfiguration.create(
        schema = setOf(
            DbVideo::class,
            DbActress::class,
            DbTag::class,
            DbPrefferedContent::class,

            )
    )
)