package net.jfsanchez.netty.handler.logging.provider

import io.netty.buffer.ByteBuf

interface BodyStatementProvider {
    fun requestBody(rawContent: ByteBuf): String
    fun responseBody(rawContent: ByteBuf): String
}
