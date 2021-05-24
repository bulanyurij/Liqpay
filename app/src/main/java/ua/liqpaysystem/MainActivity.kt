package ua.liqpaysystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ua.liqpay.LiqPay
import ua.liqpay.LiqpayCallback
import ua.liqpay.request.ErrorCode

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LiqPay(this, object: LiqpayCallback{
            override fun onSuccess(response: String?) {

            }

            override fun onError(error: ErrorCode) {

            }

        }).checkout(
            signature = "FmbJe0TaQueUjkXLGs1GLlnebW0=",
            base64Data = "eyJ2ZXJzaW9uIjozLCJwdWJsaWNfa2V5Ijoic2FuZGJveF9pNzkwOTExMzgyOTIiLCJhY3Rpb24iOiJwYXkiLCJhbW91bnQiOiIyLjAiLCJjdXJyZW5jeSI6IlVBSCIsImRlc2NyaXB0aW9uIjoiXHUwNDFmXHUwNDNlXHUwNDNmXHUwNDNlXHUwNDNiXHUwNDNkXHUwNDM1XHUwNDNkXHUwNDM4XHUwNDM1IFx1MDQ0MVx1MDQ0N1x1MDQzNVx1MDQ0Mlx1MDQzMCBcdTA0MzIgXHUwNDNmXHUwNDQwXHUwNDM4XHUwNDNiXHUwNDNlXHUwNDM2XHUwNDM1XHUwNDNkXHUwNDM4XHUwNDM4IEhlbHAmSm9iIiwib3JkZXJfaWQiOiI3NzkzNzE3Ni1iZDQzLTQ4MTAtYTBmYi01ODU2YmQ3ZjQ4OGIiLCJzZXJ2ZXJfdXJsIjoiaHR0cHM6XC9cL2hlbHBuam9iLmlkZWlsLmNvbVwvYXBpXC9saXFwYXlcL2NhbGxiYWNrIn0=")
    }
}