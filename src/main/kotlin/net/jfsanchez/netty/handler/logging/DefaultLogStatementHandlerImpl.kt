package net.jfsanchez.netty.handler.logging

import io.netty.channel.Channel
import java.util.logging.Level
import java.util.logging.Logger

class DefaultLogStatementHandlerImpl(
    private val logger: Logger = Logger.getLogger(DefaultLogStatementHandlerImpl::class.java.name),
    private val logLevel: Level = Level.FINE,
) : LogStatementHandler {
    override fun handle(ctx: Channel, statement: String) {
        logger.log(logLevel, statement)
    }
}
