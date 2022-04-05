package com.kevvlvl.observableapi.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function


@Aspect
@Component
class LoggingAspect {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Around("@annotation(LogFlow)")
    @Throws(Throwable::class)
    fun logFunctionCall(joinPoint : ProceedingJoinPoint): Any {

        val funcSignature = joinPoint.signature

        logger.debug("BEGIN - Method [$funcSignature] - Inputs = [${joinPoint.args}] ")

        val stopWatch = StopWatch()
        stopWatch.start()

        val output = joinPoint.proceed()

        if (output is Mono<*>) {

            return output.flatMap(Function {
                stopWatch.stop()
                logger.debug("END Method [$funcSignature] (${stopWatch.totalTimeMillis} ms) - Return = [$it]")
                Mono.just(it)
            })

        } else if (output is Flux<*>) {

            return output.collectList()
                .flatMapMany {
                    stopWatch.stop()
                    logger.debug("END - Method [$funcSignature] (${stopWatch.totalTimeMillis} ms) - Return = [$it]")
                    Flux.fromIterable(it)
                }

        } else {
            stopWatch.stop()
            logger.debug("END Method [$funcSignature] (${stopWatch.totalTimeMillis} ms) - Return = [$output]")
            return output
        }
    }
}