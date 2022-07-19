package net.jfsanchez.netty.handler.logging.provider.impl

import io.netty.buffer.ByteBuf
import java.nio.charset.Charset
import net.jfsanchez.netty.handler.logging.provider.BodyStatementProvider

class DefaultBodyStatementProviderImpl : BodyStatementProvider {
    override fun requestBody(rawContent: ByteBuf) = "body:\n" + rawContent.toString(Charset.defaultCharset())
    override fun responseBody(rawContent: ByteBuf) = "body:\n" + rawContent.toString(Charset.defaultCharset())
}
