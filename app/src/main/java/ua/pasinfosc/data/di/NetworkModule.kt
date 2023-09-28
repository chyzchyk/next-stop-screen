package ua.pasinfosc.data.di

import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ua.pasinfosc.data.network.BusesService
import ua.pasinfosc.domain.usecases.GetUrlBaseUseCase

val networkModule = module {

    single {
        val apiUrl = get<GetUrlBaseUseCase>()()
        Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {
                        it.proceed(it.request().also(::println)).also(::println)
                    }
                    .build()
            )
            .build()
    }

    single { get<Retrofit>().create(BusesService::class.java) }
}