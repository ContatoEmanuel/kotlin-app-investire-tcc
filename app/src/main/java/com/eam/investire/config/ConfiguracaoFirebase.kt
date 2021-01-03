package com.eam.investire.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object ConfiguracaoFirebase {
    private var autenticacao: FirebaseAuth? = null
    private var firebase: DatabaseReference? = null

    @JvmStatic
    val firebaseDatabase: DatabaseReference?
        get() {
            if (firebase == null) {
                firebase = FirebaseDatabase.getInstance().reference
            }
            return firebase
        }

    @JvmStatic
    val firebaseAutenticacao: FirebaseAuth?
        get() {
            if (autenticacao == null) {
                autenticacao = FirebaseAuth.getInstance()
            }
            return autenticacao
        }
}