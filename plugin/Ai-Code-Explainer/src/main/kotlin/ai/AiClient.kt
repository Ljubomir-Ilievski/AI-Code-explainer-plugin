package com.ilievski.ai.plugin.ai

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AiClient(private val provider: AiProvider = GroqChatCompletionsProvider()) {

    private val client = OkHttpClient()

    fun explain(code: String, aiModel: AiModel, apiKey: String): String {

        val body = provider.buildRequestBody(code, aiModel)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(provider.endpoint)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: return "Empty response"

                if (!response.isSuccessful) {
                    return provider.parseError(responseBody, response.code)
                }

                return provider.extractContent(responseBody)
            }
        } catch (e: IOException) {
            return "Network error: ${e.message}"
        }
    }
}