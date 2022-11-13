package com.aminaja.ongap.utils


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.aminaja.ongap.databinding.WaitBinding

class Wait(val context: Context, private val layout: ViewGroup) {
    private val bind = WaitBinding.inflate(LayoutInflater.from(context))
    private val view = bind.root
    private val parent = (layout.parent as ViewGroup)

    private val param = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT,
        ConstraintLayout.LayoutParams.MATCH_PARENT
    )

    fun show(msg: String = "Please Wait...") {
        parent.removeView(view)
        bind.msg.text = msg
        view.elevation = 10F
        param.topToTop = ConstraintSet.PARENT_ID
        param.bottomToBottom = ConstraintSet.PARENT_ID

        layout.addView(view, param)
        Ui.disableEvent((context as Activity))
        Ui.show(view)
    }

    fun updateMsg(msg: String) {
        bind.msg.text = msg
    }

    fun hide() {
        Ui.enableEvent((context as Activity))
        parent.removeView(view)
        Ui.gone(view)
    }
}