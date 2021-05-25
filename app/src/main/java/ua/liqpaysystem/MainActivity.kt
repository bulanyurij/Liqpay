package ua.liqpaysystem

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ua.liqpay.LiqPay
import ua.liqpay.LiqpayCallback

private const val LIQPAY_PRIVATE_KEY = ""
private const val LIQPAY_PUBLIC_KEY = ""

class MainActivity : AppCompatActivity(), LiqpayCallback {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val liqpay = LiqPay(context = this, callback = this)
        liqpay.checkout(
            privateKey = LIQPAY_PRIVATE_KEY,
            publicKey = LIQPAY_PUBLIC_KEY,
            amount = 1.0,
            description = "test",
            orderId = "1231423423"
        )
    }

    override fun onSuccess(response: String?) {
        Log.i(TAG, "Success payment. Response: $response")
    }

    override fun onError() {
        Log.i(TAG, "An error occurred.")
    }

    override fun onCancel() {
        Log.i(TAG, "User canceled paymentю")
    }
}