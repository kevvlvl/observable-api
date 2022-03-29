package com.kevvlvl.observableapi.health

import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class PostgresHealthIndicator(dataSource: DataSource?) : DataSourceHealthIndicator(dataSource) {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    override fun doHealthCheck(builder: Health.Builder?) {

        logger.debug("doHealthCheck() START - verify DB connection and query")

        query = "SELECT count(*) as \"Count\" from car"
        super.doHealthCheck(builder)

        logger.debug("doHealthCheck() END - verified. See results of /actuator/health for details")
    }
}