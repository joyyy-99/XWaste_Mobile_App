package com.example.xwaste.utils

import com.google.firebase.firestore.FirebaseFirestore

fun saveHouseholdToDatabase(
    householdName: String,
    location: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val householdData = mapOf(
        "householdName" to householdName,
        "location" to location
    )

    db.collection("households")
        .add(householdData)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onError(exception.message ?: "Unknown error")
        }
}
