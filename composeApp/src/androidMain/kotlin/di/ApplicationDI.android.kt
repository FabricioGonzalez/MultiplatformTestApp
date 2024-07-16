package di

import com.dev.fabricio.gonzalez.mediaapp.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val datastoreModule: Module = module {
    single { createDataStore(get()) }
}