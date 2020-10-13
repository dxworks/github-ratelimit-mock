package org.dxworks.githubratelimitmock.services

import org.dxworks.githubratelimitmock.config.UserTokens
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class RateLimitService {
    private val userLimits: MutableMap<String, RateLimit> = HashMap()
    private val tokenToUser: MutableMap<String, String> = HashMap()

    fun getRateLimit(user: String): RateLimit {
        val rateLimit = userLimits(user)
        return if (rateLimit.reset < epochSecond) {
            val newRateLimit = defaultRateLimit
            userLimits[user] = newRateLimit
            newRateLimit
        } else {
            rateLimit
        }
    }

    private fun userLimits(user: String) = userLimits.computeIfAbsent(user) { defaultRateLimit }

    fun decRateLimit(user: String) {
        val rateLimit = userLimits(user)
        rateLimit.remaining = if (rateLimit.remaining > 0) rateLimit.remaining.dec() else 0
    }

    private val defaultRateLimit get() = RateLimit(10, epochSecond + 60)

    fun addUser(user: UserTokens) {
        user.tokens.forEach { tokenToUser[it] = user.name }
    }

    private val epochSecond get() = ZonedDateTime.now(ZoneId.systemDefault()).toEpochSecond()
}
