package com.kevvlvl.observableapi.listener

import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.stereotype.Component

@Component
class LogPropertiesListener {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @EventListener
    fun handleContextRefreshed(event: ContextRefreshedEvent) {
        logProperties(event.applicationContext.environment as ConfigurableEnvironment)
    }

    fun logProperties(env: ConfigurableEnvironment) {

        logger.debug("BEGIN logProperties() - START YAML configurations")

        env.propertySources
            .asSequence()
            .filter { it.name.contains("yaml") }
            .map { it as EnumerablePropertySource<*> }
            .map { it.propertyNames.toList() }
            .flatten()
            .distinctBy { it }
            .sortedBy { it }
            .forEach {
                try {

                    if(it.contains("password", true)) {
                        logger.debug(" $it=${env.getProperty(it)?.isNotEmpty()} (is not empty)")
                    } else {
                        logger.debug(" $it=${env.getProperty(it)}")
                    }

                } catch (e: Exception) {
                    logger.error("ERROR Exception when trying to get property $it -> ${e.message}")
                }
            }

        logger.debug("END logProperties() - END YAML configurations")
    }
}
