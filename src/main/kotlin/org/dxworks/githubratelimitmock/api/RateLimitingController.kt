package org.dxworks.githubratelimitmock.api

import org.dxworks.githubratelimitmock.services.AuthorizationService
import org.dxworks.githubratelimitmock.services.RateLimitService
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


    @GetMapping("/")
    fun getWithRateLimit(
            @RequestHeader("authorization") authorization: String
    ): ResponseEntity<*> {
        return ResponseEntity.ok("{}")
    }


    @GetMapping("/rate_limit")
    fun getRateLimit(
            @RequestHeader("authorization") authorization: String
    ): ResponseEntity<*> {
        val user = authorizationService.getToken(authorization)
        val rateLimit = rateLimitService.getRateLimit(user)
        return ResponseEntity(mapOf("resources" to mapOf("core" to rateLimit)), HttpStatus.OK)
    }

}
