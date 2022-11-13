package com.aminaja.ongap.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.aminaja.ongap.R
import com.aminaja.ongap.constant.Key
import com.aminaja.ongap.constant.Url
import com.aminaja.ongap.data.Session
import com.aminaja.ongap.databinding.ActivityPhoneVerificationBinding
import com.aminaja.ongap.firebase.FireStore
import com.aminaja.ongap.model.UserModel
import com.aminaja.ongap.utils.Logger
import com.aminaja.ongap.utils.Wait
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class PhoneVerification : BaseBinding<ActivityPhoneVerificationBinding>() {
    override fun binding() = ActivityPhoneVerificationBinding.inflate(layoutInflater)
    private val auth = FirebaseAuth.getInstance()
    private var tempData: Session.TemRegModel? = null
    private val fireStore = FireStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPhoneVer()
    }

    private fun initPhoneVer() {
        wait = Wait(this, bind.root)
        session.getRegTempData().let {
            if (it != null) {
                tempData = it
                if (it.expiredAt < System.currentTimeMillis()) {
                    expiredCode(true)
                    Logger.info("Expired At: ${it.expiredAt}")
                    Logger.info("Current Ml: ${System.currentTimeMillis()}")
                } else {
                    expiredCode(false)
                    bind.verMsg.text =
                        String.format(getString(R.string.auth_description), it.phoneNumber)
                    startTimer(it.expiredAt)
                }
            } else {
                finish()
            }
        }

        with(bind){
            btnOk.setOnClickListener {
                verifyCode()
            }
            btnResend.setOnClickListener {
                btnResend.isEnabled = false
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(tempData?.phoneNumber!!)       // Phone number to verify
                    .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this@PhoneVerification)                 // Activity (for callback binding)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                            signInWithPhoneAuthCredential(p0)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            if (e is FirebaseAuthInvalidCredentialsException) {
                                println("FirebaseAuthInvalidCredentialsException: ${e.message}")
                            } else if (e is FirebaseTooManyRequestsException) {
                                println("FirebaseTooManyRequestsException: ${e.message}")
                            }

                            tos.error(e.message!!)
                        }

                        override fun onCodeSent(
                            p0: String, p1: PhoneAuthProvider.ForceResendingToken
                        ) {
                            val exp = System.currentTimeMillis() + 120000
                            tempData?.resendToken = p1
                            tempData?.verificationId = p0
                            tempData?.expiredAt = exp

                            Logger.info("PHONE AUTH: $tempData")
                            session.setRegTempData(tempData!!)
                            tos.info("Kode terkirim.")
                            startTimer(exp)
                        }
                    })
                    .setForceResendingToken(tempData?.resendToken!!)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
            verifyCodeEt.setCompleteListener{
                if (it) verifyCode()
            }
        }
    }

    private fun verifyCode(){
        if (tempData == null) return

        if (bind.verifyCodeEt.text.length != 6 ){
            return
        }
        wait.show()
        val code = bind.verifyCodeEt.text
        val credential = PhoneAuthProvider.getCredential(tempData?.verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun expiredCode(b: Boolean) {
        with(bind) {
            btnOk.gone(b)
            btnResend.gone(!b)
            Logger.info("Expired Code: $b")
        }
    }

    private fun startTimer(expiredAt: Long) {
        val secondMillis = expiredAt - System.currentTimeMillis()
        val countDownTimer = object : CountDownTimer(secondMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setTimeToView(millisUntilFinished)
            }

            override fun onFinish() {
                println("timer finish")
                expiredCode(true)
            }
        }
        countDownTimer.start()
    }

    private fun setTimeToView(millisUntilFinished: Long) {
        if (millisUntilFinished == 0L){
            val text = "0 menit 0 detik"
            bind.btnOk.isEnabled = false
            bind.secondLeft.text = text
            return;
        }

        val secondLeft = millisUntilFinished / 1000
        val numberOfMinutes = secondLeft / 60
        val numberOfSeconds = secondLeft % 60

        val text = "$numberOfMinutes menit $numberOfSeconds detik"
        bind.secondLeft.text = text
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Logger.info("signInWithCredential:success")

                    session.sessionSetBool(Key.IS_NEED_VERIFICATION, false)
                    val user = UserModel(task.result?.user!!.uid, tempData?.fullName!!, tempData?.phoneNumber!!, tempData?.password!!)
                    session.saveUser(user)

                    val u = Firebase.auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = tempData?.fullName!!
                    }

                    val fUser = hashMapOf(
                        "uid" to task.result?.user!!.uid,
                        "full_name" to tempData?.fullName!!,
                        "phone_number" to tempData?.phoneNumber!!,
                        "verification_id" to tempData?.verificationId,
                        "password" to tempData?.password
                    )

                    u!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { t ->
                            if (t.isSuccessful) {
                                val path = "${Url.USERS}/${task.result?.user!!.phoneNumber}"
                                fireStore.saveData(Url.USERS, tempData?.phoneNumber!!,fUser, object : FireStore.Listener{
                                    override fun onSuccess(task: Task<Void>) {
                                        Logger.info("Firestore: User Updated")
                                    }

                                    override fun onFailure(e: Exception) {
                                        Logger.info("Firestore: $e")
                                    }
                                })

                                setTimeToView(0)
                                tos.success("Nomor anda berhasil diverifikasi")
                                wait.hide()
                                startActivity(Intent(this@PhoneVerification, Dashboard::class.java))
                            }
                        }
                } else {
                    wait.hide()
                    // Sign in failed, display a message and update the UI
                    Logger.info("signInWithCredential:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        tos.error("Kode verifikasi tidak benar!")
                        bind.verifyCodeEt.text = ""
                    }
                    // Update UI
                }
            }
    }
}