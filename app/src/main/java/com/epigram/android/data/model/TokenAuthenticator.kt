package com.epigram.android.data.model

import java.io.Serializable

data class TokenAuthenticator(
    val token: String?,
    val scope: String?,
    val expiry: String
):Serializable{
    companion object{
        fun fromTemplate(template: AuthenticatorTemplate): TokenAuthenticator? {
            return TokenAuthenticator(template.access_token, template.scope, template.expires_in)
        }
    }
}

data class AuthenticatorTemplate(
    val access_token: String?,
    val scope: String?,
    val expires_in: String
)