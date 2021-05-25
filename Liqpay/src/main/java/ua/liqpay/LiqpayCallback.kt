package ua.liqpay


/**
 * Liqpay result callback.
 */
interface LiqpayCallback {

    fun onSuccess(response: String?)

    fun onError()

    fun onCancel()
}