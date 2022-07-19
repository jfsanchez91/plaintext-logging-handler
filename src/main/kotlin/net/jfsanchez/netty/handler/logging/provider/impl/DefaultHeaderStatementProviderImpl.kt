package net.jfsanchez.netty.handler.logging.provider.impl

import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import net.jfsanchez.netty.handler.logging.provider.HeaderStatementProvider

class DefaultHeaderStatementProviderImpl : HeaderStatementProvider {
    override fun requestHeader(request: HttpRequest): String = StringBuilder().apply {
        appendLine("HTTP Request: method=${request.method()}, uri=${request.uri()}")
        request.headers().forEach { header ->
            appendLine("${header.key}: ${header.value}")
        }
    }.toString()

    override fun responseHeader(response: HttpResponse): String = StringBuilder().apply {
        appendLine("HTTP Response: status=${response.status()}")
        response.headers().forEach { header ->
            appendLine("${header.key}: ${header.value}")
        }
    }.toString()
}
