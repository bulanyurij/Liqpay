package ua.liqpay.view

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import ua.liqpay.R


class LoaderViewDialog (context: Context) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar){

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val winManager = window?.attributes
        winManager?.gravity = Gravity.CENTER
        window?.attributes = winManager
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)
        window?.setBackgroundDrawableResource(R.color.dialog_loading_background)
        window?.attributes?.windowAnimations = R.style.DialogLoading
        addContentView(view, params)
    }

}