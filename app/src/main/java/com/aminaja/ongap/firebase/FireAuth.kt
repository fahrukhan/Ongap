package com.aminaja.ongap.firebase

import android.app.Activity
import android.content.ContentValues
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aminaja.ongap.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class FireAuth(private val activity: AppCompatActivity) {
    private val auth = FirebaseAuth.getInstance()
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.WEB_CLIENT_ID)
        .requestEmail()
        .build()
    private val googleSignInClient = GoogleSignIn.getClient(activity, gso)

    val currentUser = if (auth.currentUser != null) auth.currentUser else null

    lateinit var listener: FireListener

//    fun registerEmail(email: String, pass: String) {
//        if (email.isNotEmpty() && pass.isNotEmpty()) {
//            auth.createUserWithEmailAndPassword(email, pass)
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        val user = auth.currentUser
//                        listener.onFireResult(
//                            true,
//                            user,
//                            "Register Successfully"
//                        )
//                    } else {
//                        Log.w(ContentValues.TAG, "Register Failure", it.exception)
//                        listener.onFireResult(
//                            false,
//                            it.exception,
//                            it.exception?.message!!
//                        )
//                    }
//                }
//        }
//    }

    private val signInLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val acc = auth.currentUser
                    listener.onFireResult(true, mapOf(
                        "email" to acc?.email!!,
                        "name" to acc.displayName!!,
                        "uid" to acc.uid
                    ))

                    Log.d(ContentValues.TAG, "signInWithCredential:success  => ${acc.email}")
                } else {
                    listener.onFireResult(false, mapOf(), task.exception?.message!!)
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
}