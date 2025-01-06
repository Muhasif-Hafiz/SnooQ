package com.muhasib.snooq.singleton

import android.content.Context
import com.muhasib.snooq.constants.ClientConstants
import io.appwrite.Client

object AppWriteSingleton {

    @Volatile
    private var client: Client? = null

    fun getClient(context: Context): Client {
        return client ?: synchronized(this) {
            client ?: Client(context.applicationContext)
                .setEndpoint(ClientConstants.END_POINT)
                .setProject(ClientConstants.PROJECT_ID)
                .also { client = it }
        }
    }
}
