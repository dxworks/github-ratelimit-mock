package org.dxworks.githubratelimitmock.services

import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthorizationService {
    private val basic = "Basic "
    private val bearer = "Bearer "


    fun getToken(auth: String?): String {
        return when {
            auth == null -> ""
            auth.startsWith(basic) -> getBasicToken(auth.removePrefix(basic))
            auth.startsWith(bearer) -> auth.removePrefix(bearer)
            else -> ""
        }
    }

    private fun getBasicToken(base64: String): String {
        val auth = String(Base64.getDecoder().decode(base64))
        return auth.substringAfter(":")
    }
}
