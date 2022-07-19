package net.jfsanchez.netty.handler.logging

import io.netty.channel.Channel
import java.util.logging.Level
import java.util.logging.Logger

class DefaultLogStatementHandlerImpl : LogStatementHandler {
    companion object {
        private val logger: Logger = Logger.getLogger(DefaultLogStatementHandlerImpl::class.java.name)
        private val logLevel: Level = Level.FINE
    }

    override fun handle(ctx: Channel, statement: String) {
        logger.log(logLevel, statement)
    }
}
