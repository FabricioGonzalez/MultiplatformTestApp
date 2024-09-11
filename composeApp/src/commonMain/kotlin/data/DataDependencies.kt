package data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.api.Subscription
import data.remote.actresses.ActressRemoteApi
import data.remote.tags.TagRemoteApi
import data.remote.videos.VideoRemoteApi
import data.repositories.realm.RealmActressRepository
import data.repositories.realm.RealmTagRepository
import data.repositories.realm.RealmVideoRepository
import data.repositories.realm.RealmWebLocalsRepository
import domain.repositories.ActressRepository
import domain.repositories.TagRepository
import domain.repositories.VideoRepository
import domain.repositories.WebLocalsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
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
        ApolloConnector()
    }
}

class ApolloConnector {
    private var client: ApolloClient = createClient("https://mapappapi.bsite.net/graphql/")

    private fun createClient(url: String): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(url)
            .build()
    }

    fun changeClient(url: String) {
        client = createClient(url)
    }

    suspend fun <T : Query.Data> query(query: Query<T>): ApolloResponse<T> {
        return withContext(Dispatchers.IO) { client.query(query).execute() }
    }

    suspend fun <T : Mutation.Data> mutate(mutation: Mutation<T>): ApolloResponse<T> {
        return withContext(Dispatchers.IO) { client.mutation(mutation).execute() }
    }

    fun <T : Subscription.Data> subscribe(subscription: Subscription<T>): Flow<ApolloResponse<T>> {
        return client.subscription(subscription).toFlow()
    }
}

