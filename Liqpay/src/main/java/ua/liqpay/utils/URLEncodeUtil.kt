package ua.liqpay.utils

import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder
import java.util.*

/**
 * A collection of utilities for encoding URLs.
 *
 */
object URLEncodeUtil {

    private const val DEFAULT_CONTENT_CHARSET = "UTF-8"
    private const val PARAMETER_SEPARATOR = "&"
    private const val NAME_VALUE_SEPARATOR = "="

    /**
     * Returns a list of [Pair] as built from the
     * URI's query portion. For example, a URI of
     * http://example.org/path/to/file?a=1&b=2&c=3 would return a list of three
     * NameValuePairs, one for a=1, one for b=2, and one for c=3.
     *
     *
     * This is typically useful while parsing an HTTP PUT.
     *
     * @param uri
     * uri to parse
     * @param encoding
     * encoding to use while parsing the query
     */
    fun parse(uri: URI, encoding: String?): List<Pair<String, String?>> {
        val result: MutableList<Pair<String, String?>> = mutableListOf()
        val query = uri.rawQuery
        if (query != null && query.isNotEmpty()) {
            parse(result, Scanner(query), encoding)
        }
        return result
    }

    /**
     * Adds all parameters within the Scanner to the list of
     * `parameters`, as encoded by `encoding`. For
     * example, a scanner containing the string `a=1&b=2&c=3` would
     * add the [Pair] a=1, b=2, and c=3 to the
     * list of parameters.
     *
     * @param parameters
     * List to add parameters to.
     * @param scanner
     * Input that contains the parameters to parse.
     * @param encoding
     * Encoding to use when decoding the parameters.
     */
    private fun parse(
        parameters: MutableList<Pair<String, String?>>,
        scanner: Scanner,
        encoding: String?
    ) {
        scanner.useDelimiter(PARAMETER_SEPARATOR)
        while (scanner.hasNext()) {
            val nameValue = scanner.next().split(NAME_VALUE_SEPARATOR).toTypedArray()
            require(!(nameValue.isEmpty() || nameValue.size > 2)) { "bad parameter" }
            val name = decode(nameValue[0], encoding)
            var value: String? = null
            if (nameValue.size == 2) value = decode(nameValue[1], encoding)
            parameters.add(Pair(name, value))
        }
    }

    private fun decode(content: String, encoding: String?): String {
        return try {
            URLDecoder.decode(content, encoding ?: DEFAULT_CONTENT_CHARSET)
        } catch (problem: UnsupportedEncodingException) {
            throw IllegalArgumentException(problem)
        }
    }
}