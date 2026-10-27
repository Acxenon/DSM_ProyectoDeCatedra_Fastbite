package com.example.fastbite.data

import com.example.fastbite.model.FoodOrder
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

/** Solo acceso remoto: las decisiones del carrito y la persistencia local están en FastBiteStore. */
class FirestoreOrdersDataSource(uid: String) {
    private val collection = FirebaseFirestore.getInstance()
        .collection("users").document(uid).collection("orders")

    fun loadRecent(): Task<QuerySnapshot> = collection
        .orderBy("createdAt", Query.Direction.DESCENDING).limit(100).get()

    fun save(order: FoodOrder): Task<Void> = collection.document(order.id).set(order.cloud())
}
