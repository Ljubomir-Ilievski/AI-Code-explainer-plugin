package com.ilievski.ai.plugin.ai

import org.json.JSONObject

class GroqChatCompletionsProvider(
    override val endpoint: String = "https://api.groq.com/openai/v1/chat/completions"
) : AiProvider {

    companion object {
        val MODELS = listOf(
            AiModel("llama-3.1-8b-instant", "Llama 3.1 8B (instant)"),
            AiModel("llama-3.3-70b-versatile", "Llama 3.3 70B (versatile)")
        )
    }

    override fun buildRequestBody(code: String, aiModel: AiModel): String {
        val messages = org.json.JSONArray()
            .put(JSONObject().put("role", "system").put("content", "You explain source code clearly and concisely."))
            .put(JSONObject().put("role", "user").put("content", "Explain this code:\n$code"))

        return JSONObject()
            .put("messages", messages)
            .put("model", aiModel.id)
            .put("stream", true)
            .toString()
    }

    override fun extractContent(body: String): String {
        val content = StringBuilder()

        val lines = body.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        for (line in lines) {
            if (!line.startsWith("data:")) continue

            val payload = line.removePrefix("data:").trim()
            if (payload == "[DONE]") break

            try {
                val chunk = JSONObject(payload)
                val choices = chunk.optJSONArray("choices") ?: continue

                for (i in 0 until choices.length()) {
                    val deltaContent = choices
                        .optJSONObject(i)
                        ?.optJSONObject("delta")
                        ?.optString("content")

                    if (!deltaContent.isNullOrEmpty()) {
                        content.append(deltaContent)
                    }
                }
            } catch (_: Exception) {
                // Ignore malformed stream chunks and continue parsing.
            }
        }

        return content.toString().ifBlank { "Failed to parse response" }
    }

    override fun parseError(body: String, statusCode: Int): String {
        return try {
            val json = JSONObject(body)
            val error = json.optJSONObject("error")

            val code = error?.optString("code")?.takeIf { it.isNotBlank() } ?: "unknown"
            val message = error?.optString("message")?.takeIf { it.isNotBlank() } ?: "Unknown error"

            "API error ($code): $message"
        } catch (_: Exception) {
            "HTTP $statusCode error: $body"
        }
    }
}