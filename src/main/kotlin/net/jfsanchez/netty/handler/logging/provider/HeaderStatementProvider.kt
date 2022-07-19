package net.jfsanchez.netty.handler.logging.provider

import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse

interface HeaderStatementProvider {
    fun requestHeader(request: HttpRequest): String
    fun responseHeader(response: HttpResponse): String
}
