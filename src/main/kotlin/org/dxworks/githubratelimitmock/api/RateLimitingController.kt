package org.dxworks.githubratelimitmock.api

import org.dxworks.githubratelimitmock.services.AuthorizationService
import org.dxworks.githubratelimitmock.services.RateLimit
import org.dxworks.githubratelimitmock.services.RateLimitService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class RateLimitingController(
        private val authorizationService: AuthorizationService,
        private val rateLimitService: RateLimitService
) {
    private val rateLimitExceeded = """{
   "message": "API rate limit exceeded for xxx.xxx.xxx.xxx. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)",
   "documentation_url": "https://developer.github.com/v3/#rate-limiting"
}"""

    @GetMapping("/")
    fun getWithRateLimit(
            @RequestHeader("authorization") authorization: String
    ): ResponseEntity<*> {
        val user = authorizationService.getToken(authorization)
        rateLimitService.decRateLimit(user)
        val rateLimit = rateLimitService.getRateLimit(user)
        return wrapResponse("{}", rateLimit);
    }

    private fun wrapResponse(body: Any, rateLimit: RateLimit): ResponseEntity<*> {
        return if (rateLimit.remaining == 0L) {
            ResponseEntity(rateLimitExceeded, headers(rateLimit), HttpStatus.FORBIDDEN)
        } else ResponseEntity(body, headers(rateLimit), HttpStatus.OK)
    }

    private fun headers(rateLimit: RateLimit): HttpHeaders {
        return HttpHeaders().apply {
            put("X-RateLimit-Limit", listOf(rateLimit.limit.toString()))
            put("X-RateLimit-Remaining", listOf(rateLimit.remaining.toString()))
            put("X-RateLimit-Reset", listOf(rateLimit.reset.toString()))
        }
    }

    @GetMapping("/rate_limit")
    fun getRateLimit(
            @RequestHeader("authorization") authorization: String
    ): ResponseEntity<*> {
        val user = authorizationService.getToken(authorization)
        val rateLimit = rateLimitService.getRateLimit(user)
        return ResponseEntity(
                mapOf("resources" to mapOf("core" to rateLimit)),
                headers(rateLimit),
                HttpStatus.OK)
    }

}
