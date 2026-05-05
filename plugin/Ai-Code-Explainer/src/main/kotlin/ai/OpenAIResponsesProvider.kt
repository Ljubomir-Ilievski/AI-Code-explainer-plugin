package com.ilievski.ai.plugin.ai

import org.json.JSONObject

class OpenAIResponsesProvider(override val endpoint: String = "https://api.openai.com/v1/responses") : AiProvider {

    companion object {
        val MODELS = listOf(
            AiModel("gpt-4o-mini", "Fast (cheap)"),
            AiModel("gpt-4.1-mini", "Balanced"),
            AiModel("gpt-4.1", "Best quality")
        )
    }

    override fun buildRequestBody(code: String, aiModel: AiModel): String {
        return JSONObject()
            .put("model", aiModel.id)
            .put("input", "Explain this code:\n$code")
            .toString()
    }

    override fun extractContent(body: String): String {
        return try {
            val json = JSONObject(body)

            val outputText = json.optString("output_text")
            if (outputText.isNotBlank()) {
                return outputText
            }

            val output = json.optJSONArray("output")
            if (output != null && output.length() > 0) {
                val text = output
                    .getJSONObject(0)
                    .optJSONArray("content")
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
            val error = json.getJSONObject("error")

            val code = error.optString("code", "unknown")
            val message = error.optString("message", "Unknown error")

            when (code) {
                "insufficient_quota" -> "Quota exceeded. Check billing."
                else -> "API error ($code): $message"
            }
        } catch (_: Exception) {
            "HTTP $statusCode error: $body"
        }
    }
}