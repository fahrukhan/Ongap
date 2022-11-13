package com.aminaja.ongap.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.aminaja.ongap.data.Session
import com.aminaja.ongap.utils.Tos
import com.aminaja.ongap.utils.Wait

abstract class BaseBinding<B: ViewBinding>: AppCompatActivity() {
    lateinit var bind: B
    lateinit var session: Session
    lateinit var tos: Tos
    lateinit var wait: Wait


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = binding()
        setContentView(bind.root)

        initBind()
    }

    abstract fun binding(): B
    private fun initBind(){
        tos = Tos(this)
        session = Session(this)
    }

    protected fun View.gone(b: Boolean = true) {
        this.visibility = if (b) View.GONE else View.VISIBLE
    }
    protected fun View.show() { this.visibility = View.VISIBLE }
}