package di

import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import data.entities.dbSet
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.core.module.Module
import org.koin.dsl.module

actual val datastoreModule: Module = module {
    single {
        Realm.open(
            configuration = RealmConfiguration.create(
                schema = dbSet
            )
        )
    }
    single {
        SqlNormalizedCacheFactory("media_app_cache")
    }
}