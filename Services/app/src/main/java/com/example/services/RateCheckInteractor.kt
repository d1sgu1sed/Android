package com.example.services

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RateCheckInteractor {
    class GsonResponseModel{
        val RUB: Float = 0.0F
    }
    val networkClient = NetworkClient()

    suspend fun requestRate(): String {
        return withContext(Dispatchers.IO) {
            val result = networkClient.request(MainViewModel.USD_RATE_URL)
            if (!result.isNullOrEmpty()) {
                parseRate(result)
            } else {
                ""
            }
        }
    }

    private fun parseRate(jsonString: String): String {
        try {
            var gson = Gson()
            val tree = gson.fromJson(jsonString, GsonResponseModel::class.java)
            return tree.RUB.toString()

        } catch (e: Exception) {
            Log.e("RateCheckInteractor", "", e)
        }
        return ""
    }


}