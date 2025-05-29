package com.kidshield.childapp

import com.google.firebase.firestore.FirebaseFirestore
class ChildProfileManager {
    private val db = FirebaseFirestore.getInstance()
    private val childProfilesCollection = db.collection("child_profiles")

    fun saveChildProfile(profile: ChildProfile) {
        childProfilesCollection.document(profile.id).set(profile)
    }

    fun getChildProfile(id: String, callback: (ChildProfile?) -> Unit) {
        childProfilesCollection.document(id).get().addOnSuccessListener {
            val profile = it.toObject(ChildProfile::class.java)
            callback(profile)
        }.addOnFailureListener {
            callback(null)
        }
    }
}