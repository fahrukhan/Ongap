package com.aminaja.ongap.utils

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aminaja.ongap.R
import es.dmoral.toasty.Toasty

class Tos(private val context: Context) {

    init {
        Toasty.Config.getInstance()
            .setGravity(Gravity.BOTTOM)
            .setToastTypeface(Typeface.MONOSPACE)
            .setTextSize(12)
            .apply()
    }

    fun success(msg: String, duration: Int = SHORT_TOA, withIcon: Boolean = true){
        Toasty.custom(
            context,
            msg,
            R.drawable.ic_check_24,
            R.color.green_400,
            duration,
            withIcon,
            true
        ).show()
    }

    fun info(msg: String, duration: Int = SHORT_TOA, isDark: Boolean = false, withIcon: Boolean = true ){
        //Toasty.info(context, msg, duration).show(
        var textColor = ContextCompat.getColor(context, R.color.white)
        var bgColor = ContextCompat.getColor(context, R.color.toastInfoColor)

        if (isDark) {
            textColor = ContextCompat.getColor(context, R.color.toastInfoColor)
            bgColor = ContextCompat.getColor(context, R.color.white)
        }

        else ContextCompat.getColor(context, R.color.white)
        Toasty.custom(
            context,
            msg,
            ContextCompat.getDrawable(context, R.drawable.ic_info_24),
            bgColor, //bgColor
            textColor, //textColor
            duration,
            withIcon,
            true
        ).show()
    }

    fun warning(msg: String, duration: Int = SHORT_TOA, withIcon: Boolean = true){
        Toasty.custom(
            context,
            msg,
            R.drawable.ic_warning_24,
            R.color.amber_400,
            duration,
            withIcon,
            true
        ).show()
    }

    fun error(msg: String, duration: Int = SHORT_TOA, withIcon: Boolean = true){
        var dur = duration
        if (msg.length > 80) {
            dur = LONG_TOA
        }

        Toasty.custom(
            context,
            msg,
            R.drawable.ic_error_24,
            R.color.red_400,
            dur,
            withIcon,
            true
        ).show()
    }

    companion object {
        const val LONG_TOA = Toast.LENGTH_LONG
        const val SHORT_TOA = Toast.LENGTH_SHORT
    }

}