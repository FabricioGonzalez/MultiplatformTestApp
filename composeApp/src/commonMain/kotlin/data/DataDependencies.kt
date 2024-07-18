package data

import com.apollographql.apollo3.ApolloClient
import data.remote.ActressRemoteApi
import data.remote.TagRemoteApi
import data.remote.VideoRemoteApi
import data.repositories.realm.RealmActressRepository
import data.repositories.realm.RealmTagRepository
import data.repositories.realm.RealmVideoRepository
import data.repositories.realm.RealmWebLocalsRepository
import domain.repositories.ActressRepository
import domain.repositories.TagRepository
import domain.repositories.VideoRepository
import domain.repositories.WebLocalsRepository
import org.koin.dsl.module

val dataDependencies = module {

    single { VideoRemoteApi(get(), get()) }
    single { ActressRemoteApi(get(), get()) }
    single { TagRemoteApi(get()) }

    single<VideoRepository> { RealmVideoRepository(get(), get()) }
    single<ActressRepository> { RealmActressRepository(get(), get()) }
    single<TagRepository> { RealmTagRepository(get()) }
    single<WebLocalsRepository> { RealmWebLocalsRepository(get()) }

    /*single<VideoRepository> { RoomVideoRepository(get(), get()) }
    single<ActressRepository> { RoomActressRepository(get(), get()) }
    single<TagRepository> { RoomTagRepository(get()) }
    single<WebLocalsRepository> { RoomWebLocalsRepository(get()) }*/
}

val apolloDependencies = module {
    single {
        ApolloClient.Builder()
            /*.serverUrl("https://mediaapi.bsite.net/graphql")*/
            /*.serverUrl("https://testmediaapi.bsite.net/graphql/")*/
            .serverUrl("https://mapappapi.bsite.net/graphql/")
            /*.serverUrl("http://localhost:3001/graphql/")*/
            .build()
    }
}

