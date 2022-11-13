package com.aminaja.ongap.firebase

interface FireListener {
    fun onFireResult(success: Boolean, data: Map<String, String> = mapOf(), msg: String = "success")
}