package com.aminaja.ongap.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.aminaja.ongap.R
import com.aminaja.ongap.constant.SignMode
import com.aminaja.ongap.constant.SignMode.*
import com.aminaja.ongap.data.Session.TemRegModel
import com.aminaja.ongap.databinding.ActivityMainBinding
import com.aminaja.ongap.utils.Format
import com.aminaja.ongap.utils.Logger
import com.aminaja.ongap.utils.Ui
import com.aminaja.ongap.utils.Wait
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class MainActivity : BaseBinding<ActivityMainBinding>() {
    override fun binding() = ActivityMainBinding.inflate(layoutInflater)
    private val auth = FirebaseAuth.getInstance()
    private var signMode = LOGIN
    private var user: FirebaseUser? = null

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            Ui.intent<Dashboard>(this)
        }
        session.apply {
            if (isNeedVerification()) {
                getRegTempData().let {
                    if (it != null) {
                        if (it.expiredAt > System.currentTimeMillis()) {
                            startActivity(Intent(this@MainActivity, PhoneVerification::class.java))
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initMain()
    }

    private fun initMain() {
        wait = Wait(this, bind.root)
        auth.setLanguageCode("id")

        with(bind) {
            mainReadyAction.setOnClickListener {
                passwordEt.setText("")
                passwordConfirmEt.setText("")
                doSign(if (signMode == REGISTER) REGISTER_FIELD else LOGIN_FIELD)
            }
            loginBtn.setOnClickListener {
                doSign(signMode)
            }
            phoneEt.doOnTextChanged { _, _, _, _ ->
                mainPhoneLy.isErrorEnabled = false
            }
        }
    }

    private fun preSign(): Boolean {
        with(bind) {
            val fullName = fullNameEt.text.toString()
            if (fullName.isEmpty() && signMode == REGISTER) {
                mainNameLy.error = getString(R.string.warning_empty_field)
                return false
            }
            val number = phoneEt.text.toString()
            if (number.isEmpty()) {
                mainPhoneLy.error = "Please fill out the field!"
                return false
            }
        }
        return true
    }

    private fun getField(): TemRegModel {
        with(bind) {
            return TemRegModel(
                fullNameEt.text.toString(),
                passwordEt.text.toString(),
                0,
                phoneEt.text.toString(),
                null,
                null
            )
        }
    }

    private fun doSign(sign: SignMode) {
        with(bind) {
            when (sign) {
                REGISTER -> {
                    if (!preSign()) return
                    wait.show()
                    val phoneNumb = Format.phoneNumberFormatter(getField().phoneNumber)!!
                    val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumb)
                        .setTimeout(120L, TimeUnit.SECONDS).setActivity(this@MainActivity)
                        .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                                signInWithPhoneAuthCredential(p0)
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                if (e is FirebaseAuthInvalidCredentialsException) {
                                    println("FirebaseAuthInvalidCredentialsException: ${e.message}")
                                } else if (e is FirebaseTooManyRequestsException) {
                                    println("FirebaseTooManyRequestsException: ${e.message}")
                                }

                                wait.hide()
                                tos.error(e.message!!)
                            }

                            override fun onCodeSent(
                                p0: String, p1: PhoneAuthProvider.ForceResendingToken
                            ) {
                                val exp = System.currentTimeMillis() + 120000
                                Logger.info("PASSWORD: ${getField()}")
                                session.setRegTempData(
                                    TemRegModel(
                                        getField().fullName, getField().password, exp,
                                        phoneNumb, p0, p1
                                    )
                                )
                                tos.info("Kode terkirim.")
                                wait.hide()
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        PhoneVerification::class.java
                                    )
                                )
                            }
                        }).build()

                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
                LOGIN -> {
                    if (!preSign()) return
                    wait.show()
                    val phoneNumb = Format.phoneNumberFormatter(getField().phoneNumber)!!
                    val tempData = session.getRegTempData()!!
                    val credential =
                        PhoneAuthProvider.getCredential(tempData.verificationId!!, "863896")
                    signInWithPhoneAuthCredential(credential)
                }
                LOGIN_FIELD -> {
                    signMode = REGISTER
                    loginBtn.text = getString(R.string.btn_register)
                    mainTitle.text = getString(R.string.btn_register)
                    mainReadyAction.text = getString(R.string.sign_in_text)
                    mainReadyAsk.text = getString(R.string.login_ask)
                    Ui.show(arrayOf(mainNameLy, mainConfirmLy))
                }
                REGISTER_FIELD -> {
                    signMode = LOGIN
                    loginBtn.text = getString(R.string.btn_login)
                    mainTitle.text = getString(R.string.btn_login)
                    mainReadyAction.text = getString(R.string.sign_up_text)
                    mainReadyAsk.text = getString(R.string.register_ask)
                    Ui.gone(arrayOf(mainNameLy, mainConfirmLy))
                }
            }
        }

    }

    private fun signInWithPhoneAuthCredential(p0: PhoneAuthCredential) {
        auth.signInWithCredential(p0).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Ui.intent<Dashboard>(this)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    println(task.exception?.message!!)
                }
            }
            wait.hide()
        }
    }

}

fun main() {
    val a = System.currentTimeMillis()
    val b = System.currentTimeMillis() + 120000
    val c = (b - a) / 1000
    println(a)
    println(b)
    println(c)
}