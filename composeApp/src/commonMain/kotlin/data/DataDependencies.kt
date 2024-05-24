package data

import com.apollographql.apollo3.ApolloClient
import data.remote.ActressRemoteApi
import data.remote.TagRemoteApi
import data.remote.VideoRemoteApi
import data.repositories.VideoRepository
import org.koin.dsl.module

val dataDependencies = module {
    single { VideoRemoteApi(get()) }
    single { ActressRemoteApi(get()) }
    single { TagRemoteApi(get()) }

    single { VideoRepository(get()) }
}

val apolloDependencies = module {
    single {
        ApolloClient.Builder()
            .serverUrl("https://mediaapi.bsite.net/graphql")
            .build()
    }
}

