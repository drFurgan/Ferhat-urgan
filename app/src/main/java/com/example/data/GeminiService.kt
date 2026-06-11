package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    // Using the recommended default model for basic/complex text tasks
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Sends a chat prompt to Gemini with system instructions to provide soccer-expert analyses
     * for the World Cup 2026.
     */
    suspend fun askAssistant(prompt: String, chatHistory: List<Pair<String, Boolean>> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is missing or default!")
            return@withContext "Hata: Google AI API anahtarı yapılandırılmadı. Lütfen AI Studio Secrets panelinden 'GEMINI_API_KEY' anahtarını ekleyin."
        }

        try {
            // Setup system instructions
            val systemInstruction = "Sen FIFA Dünya Kupası 2026 Futbol Yapay Zekası uzmanısın. Bugünün tarihi 11 Haziran 2026 ve turnuvanın açılış günü! " +
                    "ABD, Meksika ve Kanada ortaklığında düzenlenen bu turnuvada 48 takım ve 12 grup (A-L) bulunuyor. " +
                    "Kullanıcılara Türkçe dilinde, kibar, coşkulu, futbol terminolojisine hakim ve rasyonel analizler, kadro durumları ve maç tahminleri sunacaksın. " +
                    "Yorumlarını rasyonel güç dengeleri (örneğin Arjantin, Fransa, Brezilya, İspanya, Türkiye'nin kadroları) üzerine kurmalısın."

            // Build request JSON programmatically using built-in org.json package
            val requestJson = JSONObject()
            
            // Contents array (History + current prompt)
            val contentsArray = JSONArray()

            // 1. Add historical chat context
            chatHistory.forEach { (msg, isUser) ->
                val contentObj = JSONObject()
                contentObj.put("role", if (isUser) "user" else "model")
                
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", msg)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                
                contentsArray.put(contentObj)
            }

            // 2. Add current user prompt
            val currentUserContent = JSONObject()
            currentUserContent.put("role", "user")
            val partsArray = JSONArray()
            val currentPartObj = JSONObject()
            currentPartObj.put("text", prompt)
            partsArray.put(currentPartObj)
            currentUserContent.put("parts", partsArray)
            contentsArray.put(currentUserContent)

            requestJson.put("contents", contentsArray)

            // Add system instruction
            val systemInstObj = JSONObject()
            val sysPartsArray = JSONArray()
            val sysPartObj = JSONObject()
            sysPartObj.put("text", systemInstruction)
            sysPartsArray.put(sysPartObj)
            systemInstObj.put("parts", sysPartsArray)
            requestJson.put("systemInstruction", systemInstObj)

            // Configuration
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            requestJson.put("generationConfig", genConfig)

            val requestBodyString = requestJson.toString()
            Log.d(TAG, "Request payload length: ${requestBodyString.length}")

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBodyString.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e(TAG, "Unsuccessful response from Gemini API: Code ${response.code}, Body: $errBody")
                return@withContext "API Hatası (Kod ${response.code}): Lütfen API anahtarınızın geçerli ve aktif olduğunu doğrulayın."
            }

            val bodyString = response.body?.string()
            if (bodyString.isNullOrEmpty()) {
                return@withContext "Sunucudan boş yanıt döndü."
            }

            // Parse response json
            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val responseContent = firstCandidate.optJSONObject("content")
                if (responseContent != null) {
                    val parts = responseContent.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "Cevap metni bulunamadı.")
                    }
                }
            }

            return@withContext "Metin cevabı bulunamadı. Lütfen daha sonra tekrar deneyin."
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini: ", e)
            return@withContext "Hata oluştu: ${e.localizedMessage ?: "Bilinmeyen bağlantı hatası."}"
        }
    }
}
