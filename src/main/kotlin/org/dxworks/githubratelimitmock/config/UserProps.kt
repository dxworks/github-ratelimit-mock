package org.dxworks.githubratelimitmock.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*

@Component
@ConfigurationProperties(prefix = "users")
class UserProps : ArrayList<UserTokens>() {
}

