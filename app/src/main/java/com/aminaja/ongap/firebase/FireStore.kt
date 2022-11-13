package com.aminaja.ongap.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FireStore {
    private val db = Firebase.firestore

    fun saveData(coll: String, doc: String, data: HashMap<String, *>, listener: Listener){
        db.collection(coll).document(doc).set(data).addOnCompleteListener { listener.onSuccess(it) }
            .addOnFailureListener{ listener.onFailure(it) }
    }

    fun updateData(coll: String, doc: String, data: HashMap<String, *>, listener: Listener){
        db.collection(coll).document(doc).update(data).addOnCompleteListener { listener.onSuccess(it) }
            .addOnFailureListener{ listener.onFailure(it) }
    }

    interface Listener{
        fun onSuccess(task: Task<Void>) {}
        fun onFailure(e: Exception){}
    }
}