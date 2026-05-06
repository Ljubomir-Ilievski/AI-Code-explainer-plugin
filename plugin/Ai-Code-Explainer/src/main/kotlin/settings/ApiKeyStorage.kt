package com.ilievski.ai.plugin.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe

class ApiKeyStorage {

    companion object {
        private const val SERVICE_NAME = "AI Code Explainer OpenAI API Key"
        private const val USER_NAME = "openai"
    }

    private val attributes = CredentialAttributes(SERVICE_NAME, USER_NAME)

    fun getApiKey(): String? {
        return PasswordSafe.instance.get(attributes)?.getPasswordAsString()?.trim().takeUnless { it.isNullOrBlank() }
    }

    fun setApiKey(apiKey: String?) {
        val normalized = apiKey?.trim().orEmpty()
        val credentials = if (normalized.isBlank()) null else Credentials(USER_NAME, normalized)
        PasswordSafe.instance.set(attributes, credentials)
    }
}
