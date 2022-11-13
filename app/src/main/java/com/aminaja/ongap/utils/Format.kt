package com.aminaja.ongap.utils

object Format {

    fun phoneNumberFormatter(phoneNumber: String): String? {
        var var2: String? = null

        if (phoneNumber.length < 9 || phoneNumber.length > 15){ return null }

        if (phoneNumber.take(3) == "+628") return phoneNumber

        if (phoneNumber.take(2) == "08") {
            var2 = "+62${ phoneNumber.drop(1) }"
        }

        if (phoneNumber.take(2) == "62") {
            var2 = "+62${ phoneNumber.drop(2) }"
        }

        if (phoneNumber.take(1) == "8") {
            var2 = "+62$phoneNumber"
        }

        if (var2 != null) return var2.replace(" ", "")

        return null
    }
}

//fun main() {
//    fun phoneNumberFormatter(phoneNumber: String): String? {
//        var var2 = ""
//
//        if (phoneNumber.length < 9 || phoneNumber.length > 15){ return null }
//
//        if (phoneNumber.take(3) == "+628") return phoneNumber
//
//        if (phoneNumber.take(2) == "08") {
//            var2 = phoneNumber.drop(1)
//            return "+62$var2"
//        }
//
//        if (phoneNumber.take(2) == "62") {
//            var2 = phoneNumber.drop(2)
//            return "+62$var2"
//        }
//
//        return null
//    }
//
//    val phone = "+6285291505004"
//    println(phoneNumberFormatter(phone))
//}