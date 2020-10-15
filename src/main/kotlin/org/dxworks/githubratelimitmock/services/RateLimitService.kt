package org.dxworks.githubratelimitmock.services

import org.dxworks.githubratelimitmock.config.UserTokens
import org.springframework.stereotype.Service
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

@Service
class RateLimitService {
    private val userLimits: MutableMap<String, RateLimit> = HashMap()
    private val tokenToUser: MutableMap<String, String> = HashMap()

    fun getRateLimit(token: String): RateLimit {
        val rateLimit = userLimits(tokenToUser[token]!!)
        return if (rateLimit.reset < epochSecond) {
            val newRateLimit = defaultRateLimit
            userLimits[tokenToUser[token]!!] = newRateLimit
            newRateLimit
        } else {
            rateLimit
        }
    }

    private fun userLimits(user: String) = userLimits.computeIfAbsent(user) { defaultRateLimit }

    fun decRateLimit(token: String) {
        val rateLimit = userLimits(tokenToUser[token]!!)
        rateLimit.remaining = if (rateLimit.remaining > 0) rateLimit.remaining.dec() else 0
    }

    private val defaultRateLimit get() = RateLimit(2, epochSecond + 30)

    fun addUser(user: UserTokens) {
        user.tokens.forEach { tokenToUser[it] = user.name }
    }

    private val epochSecond get() = ZonedDateTime.now(UTC).toEpochSecond()
}
