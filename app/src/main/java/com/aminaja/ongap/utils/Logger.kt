package com.aminaja.ongap.utils

import android.content.ContentValues
import android.util.Log
import com.aminaja.ongap.constant.Url

object Logger {
    fun info(msg: Any, ignoreDev: Boolean = Url.DEVELOPMENT_MODE){
        if (ignoreDev)  Log.i(ContentValues.TAG,"INFO ==> $msg")
    }

    fun warning(msg: Any, ignoreDev: Boolean = Url.DEVELOPMENT_MODE){
        if (ignoreDev) Log.w(ContentValues.TAG,"WARNING ==> $msg")
    }

    fun error(msg: Any, ignoreDev: Boolean = Url.DEVELOPMENT_MODE){
        if (ignoreDev) Log.e(ContentValues.TAG,"ERROR ==> $msg")
    }

    fun request(msg: Any, ignoreDev: Boolean = Url.DEVELOPMENT_MODE){
        if (ignoreDev) Log.i(ContentValues.TAG,"REQUEST ==> $msg")
    }

    fun response(msg: Any, ignoreDev: Boolean = Url.DEVELOPMENT_MODE){
        if (ignoreDev) Log.i(ContentValues.TAG,"RESPONSE ==> $msg")
    }
}