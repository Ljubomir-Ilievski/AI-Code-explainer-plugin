package com.ilievski.ai.plugin.ai

object AiProviderRegistry {

    private val providers = mapOf(
        "groq" to GroqChatCompletionsProvider(),
        "gemini" to GeminiGenerateContentProvider(),
    )

    val models: List<AiModel> = listOf(
        GroqChatCompletionsProvider.MODELS,
        GeminiGenerateContentProvider.MODELS,
    ).flatten()

    fun getProvider(id: String): AiProvider? {
        return providers[id]
    }
}