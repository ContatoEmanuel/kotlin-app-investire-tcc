package com.eam.investire.model

import com.eam.investire.config.ConfiguracaoFirebase.firebaseDatabase
import com.google.firebase.database.Exclude

class Usuario {
    private var idUsuario: String? = null
    var nome: String? = null
    var email: String? = null

    @get:Exclude
    var senha: String? = null
    val receitaTotal = 0.00
    val despesaTotal = 0.00
    fun setIdUsuario(idUsuario: String?) {
        this.idUsuario = idUsuario
    }

    fun salvar() {
        val firebase = firebaseDatabase
        firebase!!.child("usuarios")
                .child(idUsuario!!)
                .setValue(this)
    }
}