package ua.liqpay

import ua.liqpay.request.ErrorCode


interface LiqpayCallback {

    fun onSuccess(response: String?)

    fun onError(error: ErrorCode)
}