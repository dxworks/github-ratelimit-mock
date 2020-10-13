package org.dxworks.githubratelimitmock.services

import org.dxworks.githubratelimitmock.config.UserProps
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userProps: UserProps,
        private val rateLimitService: RateLimitService
) {


    init {
        userProps.forEach(rateLimitService::addUser)
    }
}
