package org.dxworks.githubratelimitmock.services

class RateLimit(
        val limit: Long,
        val reset: Long,
        var remaining: Long = limit
)
