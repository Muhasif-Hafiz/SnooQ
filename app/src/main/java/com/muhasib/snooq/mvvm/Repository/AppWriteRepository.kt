package com.muhasib.snooq.mvvm.Repository

import android.content.Context
import android.util.Log
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.constants.userDetail
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Account

class AppWriteRepository(val context: Context) {

  private val _client = AppWriteSingleton.getClient(context)
  val client: Client get() = _client
  private lateinit var  sharedPrefences : MySharedPreferences

  suspend fun sendEmail(userEmail: String) {
    val account = Account(client)

    try {
      // Creating an email token requires both userId and email
      val sessionToken = account.createEmailToken(
        userId = ID.unique(), // Generates a unique user ID
        email = userEmail
      )
      sharedPrefences = MySharedPreferences(context)

      // Save the userId
      userDetail.USER_ID = sessionToken.userId
      sharedPrefences.setToken(sessionToken.userId)
      Log.d("userId", sessionToken.userId)

      val expire = sessionToken.expire // You may want to handle the expiration

    } catch (e: Exception) {
      // Handle exceptions
      e.printStackTrace() // Consider adding more detailed error handling
    }
  }

  suspend fun loginUser(secretCode: String): Boolean {
    val account = Account(client)

    return try {

      val userId = userDetail.USER_ID
      if (userId != null) {
        // Attempt to create a session with the provided secret code
        account.createSession(
          userId = userId,
          secret = secretCode

        )
        Log.d("userId", userId)
        true
      } else {
        throw IllegalStateException("User ID is null.")
      }
    } catch (e: Exception) {

      e.printStackTrace()
      false
    }
  }

  suspend fun  logOutUser(){

     val account = Account(client)

    account.deleteSessions()
  }

}
