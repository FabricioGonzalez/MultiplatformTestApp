package data.data_access

import data.entities.DbActress
import data.entities.DbPreferredContent
import data.entities.DbTag
import data.entities.DbVideo
import data.entities.DbVideoHistory
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.io.File

actual val realmDb = Realm.open(
    configuration = RealmConfiguration.Builder(
        schema = setOf(
            DbVideo::class,
            DbActress::class,
            DbTag::class,
            DbPreferredContent::class,
            DbVideoHistory::class,
        )
    )
        .directory(File(System.getenv("APPDATA"), "MediaApp").absolutePath)
        .name("media_app_db")
        .build()
)