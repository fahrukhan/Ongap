package com.aminaja.ongap.data

import android.content.Context
import com.aminaja.ongap.constant.Key
import com.aminaja.ongap.constant.Key.IS_LOGIN
import com.aminaja.ongap.constant.Key.REGISTRATION_TEMP
import com.aminaja.ongap.constant.Key.SESSION
import com.aminaja.ongap.constant.Key.USER_DATA
import com.aminaja.ongap.model.UserModel
import com.aminaja.ongap.utils.Logger
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson

class Session(context: Context) {
    private val pref = Preference(context, SESSION)

    fun setRegTempData(temp: TemRegModel){
        pref.writeBool(Key.IS_NEED_VERIFICATION, true)
        val gson = Gson().toJson(temp)
        with(pref){
            writeString(REGISTRATION_TEMP, gson)
        }
    }

    fun sessionInfoBool(key: String): Boolean {
        return pref.readBool(key, true);
    }

    fun isNeedVerification(): Boolean {
        return pref.readBool(Key.IS_NEED_VERIFICATION, false);
    }

    fun sessionSetBool(key: String, b:Boolean) {
        pref.writeBool(key, b);
    }

    fun getRegTempData(): TemRegModel? {
        val data = pref.readString(REGISTRATION_TEMP) ?: ""
        Logger.info(data)
        return if (data.isEmpty()) null
        else {
            Gson().fromJson(data, TemRegModel::class.java)
        }
    }

    fun removeRegTempData(){
        pref.remove(REGISTRATION_TEMP)
    }

    fun isInVerificationMode(){}

    fun saveUser(user: UserModel) {
        val userToPref = Gson().toJson(user)

        with(pref) {
            writeString(USER_DATA, userToPref)
            writeBool(IS_LOGIN, true)
        }
    }

    fun getUser(): UserModel? {
        val u = pref.readString(USER_DATA) ?: ""
        return if (u.isEmpty()) null
        else Gson().fromJson(u, UserModel::class.java)
    }

    fun isLogin(): Boolean {
        return pref.readBool(IS_LOGIN, false)
    }

    fun clear() {
        with(pref) {
            remove(USER_DATA)
            writeBool(IS_LOGIN, false)
        }
    }

    data class TemRegModel(
        val fullName: String,
        val password: String,
        var expiredAt: Long,
        val phoneNumber: String,
        var verificationId: String?,
        var resendToken: PhoneAuthProvider.ForceResendingToken?
    )

}

