package com.aminaja.ongap.ui

import android.os.Bundle
import com.aminaja.ongap.constant.Url
import com.aminaja.ongap.databinding.ActivityDashboardBinding
import com.aminaja.ongap.firebase.FireStore
import com.aminaja.ongap.ui.cooperation.CooperationActivity
import com.aminaja.ongap.utils.Logger
import com.aminaja.ongap.utils.Ui
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class Dashboard : BaseBinding<ActivityDashboardBinding>() {
    override fun binding() = ActivityDashboardBinding.inflate(layoutInflater)
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FireStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initDash()
    }

    private fun initDash() {
        "Hi, ${auth.currentUser?.displayName}".also { bind.fullName.text = it }

        with(bind){
//            cooperationCard.setOnClickListener {
//                Ui.intent<CooperationActivity>(this@Dashboard)
//            }
        }
    }
}