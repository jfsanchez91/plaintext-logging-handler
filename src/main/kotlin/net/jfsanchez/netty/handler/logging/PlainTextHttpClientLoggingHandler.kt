package net.jfsanchez.netty.handler.logging

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import net.jfsanchez.netty.handler.logging.provider.BodyStatementProvider
import net.jfsanchez.netty.handler.logging.provider.HeaderStatementProvider
import net.jfsanchez.netty.handler.logging.provider.impl.DefaultBodyStatementProviderImpl
import net.jfsanchez.netty.handler.logging.provider.impl.DefaultHeaderStatementProviderImpl

open class PlainTextHttpClientLoggingHandler(
    private val headerStatementProvider: HeaderStatementProvider = DefaultHeaderStatementProviderImpl(),
    private val bodyStatementProvider: BodyStatementProvider = DefaultBodyStatementProviderImpl(),
    private val handler: LogStatementHandler = DefaultLogStatementHandlerImpl(),
) : ChannelDuplexHandler() {

    private val requestContent = StringBuilder()
    private val responseContent = StringBuilder()
    private val fullHttpRequestBuilder = FullHttpRequestBuilder()
    private val fullHttpResponseBuilder = FullHttpResponseBuilder()

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        when (msg) {
            is HttpRequest -> {
                fullHttpRequestBuilder
                    .httpVersion(msg.protocolVersion())
                    .uri(msg.uri())
                    .httpMethod(msg.method())
                    .headers(msg.headers())
            }
            is ByteBuf -> {
                fullHttpRequestBuilder.appendContent(msg)
            }
            is HttpContent -> {
                fullHttpRequestBuilder.appendContent(msg.content())
            }
        }
        super.write(ctx, msg, promise)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        when (msg) {
            is HttpResponse -> {
                fullHttpResponseBuilder
                    .httpVersion(msg.protocolVersion())
                    .headers(msg.headers())
                    .responseStatus(msg.status())
            }
            is ByteBuf -> {
                fullHttpResponseBuilder.appendContent(msg)
            }
            is HttpContent -> {
                fullHttpResponseBuilder.appendContent(msg.content())
            }
        }
        ctx.fireChannelRead(msg)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logAndReset(ctx)
        super.channelInactive(ctx)
    }

    private fun logAndReset(ctx: ChannelHandlerContext) {
        fullHttpRequestBuilder.build().let { request ->
            requestContent.append(headerStatementProvider.requestHeader(request))
            if (request.content().readableBytes() > 0) {
                requestContent.appendLine(bodyStatementProvider.requestBody(request.content()))
            }
            request.content().release()
        }

        fullHttpResponseBuilder.build().let { response ->
            responseContent.append(headerStatementProvider.responseHeader(response))
            if (response.content().readableBytes() > 0) {
                responseContent.appendLine(bodyStatementProvider.responseBody(response.content()))
            }
            response.content().release()
        }

        StringBuilder().let { statement ->
            statement.appendLine(requestContent.toString()).appendLine(responseContent.toString())
            requestContent.clear()
            responseContent.clear()
            handler.handle(ctx.channel(), statement.toString())
        }
    }

    class FullHttpResponseBuilder {
        private lateinit var httpVersion: HttpVersion
        private lateinit var responseStatus: HttpResponseStatus
        private val content: ByteBuf = ByteBufAllocator.DEFAULT.buffer()
        private lateinit var headers: HttpHeaders

        fun httpVersion(httpVersion: HttpVersion) = apply { this.httpVersion = httpVersion }
        fun responseStatus(responseStatus: HttpResponseStatus) = apply { this.responseStatus = responseStatus }
        fun appendContent(content: ByteBuf) = this.also {
            this.content.writeBytes(content)
        }

        fun headers(headers: HttpHeaders) = apply { this.headers = headers }
        fun build() = DefaultFullHttpResponse(httpVersion, responseStatus, content, headers, headers)
    }

    class FullHttpRequestBuilder {
        private lateinit var httpVersion: HttpVersion
        private lateinit var httpMethod: HttpMethod
        private lateinit var uri: String
        private val content: ByteBuf = ByteBufAllocator.DEFAULT.buffer()
        private lateinit var headers: HttpHeaders

        fun httpVersion(httpVersion: HttpVersion) = apply { this.httpVersion = httpVersion }
        fun httpMethod(httpMethod: HttpMethod) = apply { this.httpMethod = httpMethod }
        fun uri(uri: String) = apply { this.uri = uri }
        fun appendContent(content: ByteBuf) = this.also {
            this.content.writeBytes(content)
        }

        fun headers(headers: HttpHeaders) = apply { this.headers = headers }
        fun build() = DefaultFullHttpRequest(httpVersion, httpMethod, uri, content, headers, headers)
    }
}
