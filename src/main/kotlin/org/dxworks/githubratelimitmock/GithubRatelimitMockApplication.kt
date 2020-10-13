package org.dxworks.githubratelimitmock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GithubRatelimitMockApplication

fun main(args: Array<String>) {
	runApplication<GithubRatelimitMockApplication>(*args)
}
