package di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.dev.fabricio.gonzalez.mediaapp.createDataStore
import data.entities.dbSet
import data.room.DatabaseSpecs
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.core.module.Module
import org.koin.dsl.module

actual val datastoreModule: Module = module {
    single { createDataStore(get()) }
    single { param ->
        val dbFile = param.get<Context>().getDatabasePath("media_app_db.db")

        Room.databaseBuilder<DatabaseSpecs>(
            context = param.get<Context>().applicationContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { param ->
        val dbFile = param.get<Context>().getDatabasePath("media_app_cache.db").absolutePath

        SqlNormalizedCacheFactory(dbFile)
    }

    single {
        Realm.open(
            configuration = RealmConfiguration.create(
                schema = dbSet
            )
        )
    }
}