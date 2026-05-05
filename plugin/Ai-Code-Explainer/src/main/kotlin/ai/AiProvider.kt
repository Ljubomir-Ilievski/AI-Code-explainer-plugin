package com.ilievski.ai.plugin.ai

interface AiProvider {

    val endpoint: String

    fun buildRequestBody(code: String, aiModel: AiModel): String

    fun extractContent(body: String): String

    fun parseError(body: String, statusCode: Int): String
}