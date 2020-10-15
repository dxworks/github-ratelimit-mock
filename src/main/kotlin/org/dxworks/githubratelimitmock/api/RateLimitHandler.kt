package org.dxworks.githubratelimitmock.api

import org.dxworks.githubratelimitmock.services.AuthorizationService
import org.dxworks.githubratelimitmock.services.RateLimit
import org.dxworks.githubratelimitmock.services.RateLimitService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RateLimitHandler(
        private val rateLimitService: RateLimitService,
        private val authorizationService: AuthorizationService
) : HandlerInterceptor {

    private val rateLimitExceeded = """{
   "message": "API rate limit exceeded for xxx.xxx.xxx.xxx. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)",
   "documentation_url": "https://developer.github.com/v3/#rate-limiting"
}"""

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val user = authorizationService.getToken(request.getHeader("Authorization"))
        val rateLimit = rateLimitService.getRateLimit(user)

        println("[${request.requestURI}] $user -> ${rateLimit.remaining} ${LocalDateTime.ofEpochSecond(rateLimit.reset, 0, ZoneOffset.UTC)}\n")

        if (request.requestURI.toString() == "/rate_limit") {
            addRateLimitHeaders(rateLimit, response)
            return true
        }

        if (rateLimit.remaining == 0L) {
            response.status = HttpStatus.FORBIDDEN.value()
            response.writer.print(rateLimitExceeded)
        } else {
            response.writer.print("{}")
            rateLimitService.decRateLimit(user)
        }
        addRateLimitHeaders(rateLimit, response)
        return false
    }

    private fun addRateLimitHeaders(rateLimit: RateLimit, response: HttpServletResponse) {
        response
        response.addHeader("X-RateLimit-Limit", rateLimit.limit.toString())
        response.addHeader("X-RateLimit-Remaining", rateLimit.remaining.toString())
        response.addHeader("X-RateLimit-Reset", rateLimit.reset.toString())
    }
}
