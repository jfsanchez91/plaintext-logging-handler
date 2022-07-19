package net.jfsanchez.netty.handler.logging

import io.netty.channel.Channel

interface LogStatementHandler {
    fun handle(ctx: Channel, statement: String)
}
