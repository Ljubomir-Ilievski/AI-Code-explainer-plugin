package com.ilievski.ai.plugin.ai

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import org.json.JSONObject

class AiClient {

    private val client = OkHttpClient()

    fun explain(code: String): String {
        val apiKey = System.getenv("OPENAI_API_KEY")

        if (apiKey.isNullOrBlank()) {
            return "Missing API key"
        }

        val json = JSONObject()
            .put("model", "gpt-4o-mini")
            .put("input", "Explain this code:\n$code")
            .toString()

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            json
        )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/responses")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: return "Empty response"

                if (!response.isSuccessful) {
                    return parseError(responseBody, response.code)
                }

                return extractContent(responseBody)
            }
        } catch (e: IOException) {
            return "Network error: ${e.message}"
        }
    }

    private fun parseError(body: String, statusCode: Int): String {
        return try {
            val json = JSONObject(body)
            val error = json.getJSONObject("error")

            val message = error.optString("message", "Unknown error")
            val code = error.optString("code", "unknown")

            when (code) {
                "insufficient_quota" ->
                    "Quota exceeded. Check your billing settings."

                else ->
                    "API error ($code): $message"
            }

        } catch (e: Exception) {
            "HTTP $statusCode error: $body"
        }
    }

    private fun extractContent(body: String): String {
        return try {
            val json = JSONObject(body)

            val outputText = json.optString("output_text")
            if (outputText.isNotBlank()) {
                return outputText
            }

            val output = json.optJSONArray("output")
            if (output != null && output.length() > 0) {
                val firstItem = output.getJSONObject(0)
                val content = firstItem.optJSONArray("content")
                if (content != null && content.length() > 0) {
                    val firstContent = content.getJSONObject(0)
                    val text = firstContent.optString("text")
                    if (text.isNotBlank()) {
                        return text
                    }
                }
            }

            "Failed to parse response"
        } catch (e: Exception) {
            "Failed to parse response"
        }
    }
}