package com.ilievski.ai.plugin.ai

import okhttp3.Request

interface AiProvider {

    val endpoint: String

    fun resolveEndpoint(aiModel: AiModel): String {
        return endpoint
    }

    fun buildRequestBody(code: String, aiModel: AiModel): String

    fun extractContent(body: String): String

    fun parseError(body: String, statusCode: Int): String

    fun applyAuthorization(requestBuilder: Request.Builder, apiKey: String) {
        requestBuilder.addHeader("Authorization", "Bearer $apiKey")
    }
}