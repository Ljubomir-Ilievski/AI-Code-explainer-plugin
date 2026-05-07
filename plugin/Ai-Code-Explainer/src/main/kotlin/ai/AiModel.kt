package com.ilievski.ai.plugin.ai

data class AiModel(
    val id: String,
    val displayName: String,
    val providerId: String,
    val apiKeyId: String
){
    override fun toString(): String {
        return "$displayName ($id) (${providerId})"
    }
}
