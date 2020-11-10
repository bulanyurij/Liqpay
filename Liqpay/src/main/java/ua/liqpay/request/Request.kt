package ua.liqpay.request

internal interface Request {

    fun post(url: String, params: Map<String, Any?> = hashMapOf()): String?

    fun get(url: String, query: Map<String, Any?> = hashMapOf()): String?
}