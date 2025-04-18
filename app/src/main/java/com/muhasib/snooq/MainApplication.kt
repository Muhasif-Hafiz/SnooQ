package com.muhasib.snooq

import android.app.Application
import com.muhasib.snooq.mvvm.appWriteModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@MainApplication)
            modules(listOf(appWriteModule))

        }
    }
}