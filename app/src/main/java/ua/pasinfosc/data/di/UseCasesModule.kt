package ua.pasinfosc.data.di

import org.koin.dsl.module
import ua.pasinfosc.domain.usecases.*

val useCasesModule = module {
    single { GetBusesUseCase(get()) }
    single { GetRoutesUseCase(get()) }
    single { GetBusIdUseCase(get()) }
    single { GetStopRadiusUseCase(get()) }
    single { GetTimeForAdUseCase(get()) }
    single { GetUrlBaseUseCase(get()) }
    single { GetFilesAdUseCase(get()) }
}