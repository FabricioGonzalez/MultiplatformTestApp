package di

import data.entities.dbSet
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.core.module.Module
import org.koin.dsl.module

actual val datastoreModule: Module = module {
    Realm.open(
        configuration = RealmConfiguration.create(
            schema = dbSet
        )
    )
}