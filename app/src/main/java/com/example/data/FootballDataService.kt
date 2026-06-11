package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object FootballDataService {
    private const val TAG = "FootballDataService"
    private const val BASE_URL = "https://api.football-data.org/v4"

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Fetches real football standings from football-data.org API.
     * Uses the provided X-Auth-Token header.
     */
    suspend fun fetchCompetitionStandings(apiKey: String, competition: String = "PL"): Result = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isEmpty()) {
            return@withContext Result.Error("API Key is empty")
        }

        val url = "$BASE_URL/competitions/$competition/standings"
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Auth-Token", cleanKey)
            .get()
            .build()

        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (response.isSuccessful && body.isNotEmpty()) {
                try {
                    val jsonObject = JSONObject(body)
                    val formatted = jsonObject.toString(2) // 2-space pretty print
                    Result.Success(formatted, jsonObject)
                } catch (e: Exception) {
                    Result.Error("JSON Ayrıştırma Hatası: ${e.message}")
                }
            } else {
                val errorCode = response.code
                val statusText = response.message
                Log.e(TAG, "Request failed: code=$errorCode, message=$statusText")
                if (errorCode == 403) {
                    Result.Error("API Hata [403]: Bu lige/veriye erişim izniniz yok. football-data.org ücretsiz plan sınırlarını kontrol edin.")
                } else {
                    Result.Error("API Hata [$errorCode]: $statusText")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network request failed", e)
            Result.Error("Bağlantı Hatası: İnternet bağlantınızı kontrol edin. ${e.message}")
        }
    }

    sealed interface Result {
        data class Success(val rawJson: String, val jsonObject: JSONObject) : Result
        data class Error(val message: String) : Result
    }
}
