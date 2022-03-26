package com.kevvlvl.observableapi.audit

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.server.ServerRequest

class AuditRequest {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)

        fun audit(request: ServerRequest) {

            logger.info("User {} Queried {}",
                request.headers().header("X-USER"),
                request.requestPath())
        }
    }
}