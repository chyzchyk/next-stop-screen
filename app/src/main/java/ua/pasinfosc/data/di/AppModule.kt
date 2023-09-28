package ua.pasinfosc.data.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.pasinfosc.MainViewModel

val appModule = module {
    viewModel { MainViewModel(get()) }
}