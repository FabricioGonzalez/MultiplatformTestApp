import data.apolloDependencies
import data.dataDependencies
import domain.usecaseDependecies
import features.viewModelDependencies
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            viewModelDependencies,
            usecaseDependecies,
            dataDependencies,
            apolloDependencies,
            dispatcherModule
            /*sqlDelightModule,
            mapperModule,
            platformModule()*/
        )
    }

val dispatcherModule = module {
    factory { Dispatchers.Default }
}