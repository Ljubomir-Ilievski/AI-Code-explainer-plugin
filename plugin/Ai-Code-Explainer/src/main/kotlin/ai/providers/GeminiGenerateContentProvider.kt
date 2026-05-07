package com.ilievski.ai.plugin.ai.providers

import com.ilievski.ai.plugin.ai.AiModel
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class GeminiGenerateContentProvider(
    override val endpoint: String = "https://generativelanguage.googleapis.com/v1beta/models"
) : AiProvider {

    companion object {
        val MODELS = listOf(
            AiModel("gemini-2.5-flash-lite", "Gemini 2.5 Flash-Lite", "gemini", "gemini"),
            AiModel("gemini-2.5-flash", "Gemini 2.5 Flash", "gemini", "gemini"),
            AiModel("gemini-2.5-pro", "Gemini 2.5 Pro", "gemini", "gemini")
        )
    }

    override fun resolveEndpoint(aiModel: AiModel): String {
        return "$endpoint/${aiModel.id}:generateContent"
    }

    override fun applyAuthorization(requestBuilder: Request.Builder, apiKey: String) {
        requestBuilder.addHeader("x-goog-api-key", apiKey)
    }

    override fun buildRequestBody(code: String, aiModel: AiModel): String {
        val userPrompt = "Explain this code:\n$code"
        val parts = JSONArray().put(JSONObject().put("text", userPrompt))
        val contents = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put("parts", parts)
        )

        return JSONObject()
            .put("contents", contents)
            .put("generationConfig", JSONObject().put("temperature", 0.2))
            .toString()
    }

    override fun extractContent(body: String): String {
        return try {
            val json = JSONObject(body)
            val candidates = json.optJSONArray("candidates") ?: return "Failed to parse response"

            for (i in 0 until candidates.length()) {
                val text = candidates
                    .optJSONObject(i)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    return text
                }
            }

            "Failed to parse response"
        } catch (_: Exception) {
            "Failed to parse response"
        }
    }

    override fun parseError(body: String, statusCode: Int): String {
        return try {
            val json = JSONObject(body)
            val error = json.optJSONObject("error")

            val code = error?.optString("status")?.takeIf { it.isNotBlank() } ?: "unknown"
            val message = error?.optString("message")?.takeIf { it.isNotBlank() } ?: "Unknown error"

            "API error ($code): $message"
        } catch (_: Exception) {
            "HTTP $statusCode error: $body"
        }
    }
}
