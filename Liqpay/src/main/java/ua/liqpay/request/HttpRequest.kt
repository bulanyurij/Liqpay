package ua.liqpay.request

import ua.liqpay.utils.encodeUTF8
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpRequest : Request {

    override fun post(url: String, params: Map<String, Any?>): String? {
        return perform(url, Method.POST, params)
    }

    override fun get(url: String, query: Map<String, Any?>): String? {
        return perform(url, Method.GET, query)
    }

    /**
     * Perform http request
     *
     * @param url URL to connect to
     * @param method Request type method (POST, GET, etc)
     * @param params Query or form data params
     *
     * @return Response data
     */
    private fun perform(
        url: String,
        method: Method,
        params: Map<String, Any?> = hashMapOf()
    ): String? {
        val httpClient = getHttpClient(url)
        httpClient.requestMethod = method.name
        when (method) {
            Method.POST -> {
                httpClient.doOutput = true
                val wr = DataOutputStream(httpClient.outputStream)
                wr.writeBytes(formatted(params))
                wr.flush()
                wr.close()
            }
            Method.GET -> {
            }
        }
        return try {
            val stream = BufferedInputStream(httpClient.inputStream)
            readStreamData(inputStream = stream)
        } catch (error: Exception) {
            null
        } finally {
            httpClient.disconnect()
        }
    }

    /**
     * Formatted query or form data params
     *
     * @param params Input params
     *
     * @return [String]
     */
    private fun formatted(params: Map<String, Any?>): String {
        val stringBuilder = StringBuilder()
        params.filter { it.value != null }.forEach {
            stringBuilder
                .append(it.key)
                .append("=")
                .append(it.value?.encodeUTF8())
                .append("&")
        }
        return stringBuilder.toString()
    }

    /**
     * Read data from buffer stream
     *
     * @param inputStream [BufferedInputStream]
     *
     * @return [String]
     */
    private fun readStreamData(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    /**
     * Initialization [HttpURLConnection]
     *
     * @param url URL to connect to
     *
     * @return [HttpURLConnection]
     */
    private fun getHttpClient(url: String): HttpURLConnection {
        val client = URL(url).openConnection() as HttpURLConnection
        client.defaultUseCaches = false
        return client
    }
}