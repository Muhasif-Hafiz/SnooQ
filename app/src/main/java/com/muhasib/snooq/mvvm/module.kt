package com.muhasib.snooq.mvvm

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appWriteModule = module{


    single {AppWriteRepository(androidContext()) }
  viewModel { AppWriteViewModel(get()) }
}