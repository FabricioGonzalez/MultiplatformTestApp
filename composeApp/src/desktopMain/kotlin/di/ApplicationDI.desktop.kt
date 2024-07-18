package di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.datastore.DATA_STORE_FILE_NAME
import data.datastore.createDataStore
import data.entities.dbSet
import data.room.DatabaseSpecs
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

actual val datastoreModule: Module = module {
    single {
        createDataStore {
            Path(System.getenv("APPDATA"), "MediaApp", DATA_STORE_FILE_NAME).absolutePathString()
        }
    }
    single {
        Realm.open(
            configuration = RealmConfiguration.Builder(
                schema = dbSet
            )
                .directory(File(System.getenv("APPDATA"), "MediaApp").absolutePath)
                .name("media_app_db")
                .build()
        )
    }
    single {
        Room.databaseBuilder<DatabaseSpecs>(
            name = Path(System.getenv("APPDATA"), "MediaApp", "media_app_db").absolutePathString()
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}