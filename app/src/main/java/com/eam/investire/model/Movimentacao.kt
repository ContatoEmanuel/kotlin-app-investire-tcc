package com.eam.investire.model

import com.eam.investire.config.ConfiguracaoFirebase.firebaseAutenticacao
import com.eam.investire.config.ConfiguracaoFirebase.firebaseDatabase
import com.eam.investire.helper.Base64Custom.codificarBase64
import com.eam.investire.helper.DateCustom.mesAnoDataEscolhida
import java.util.*

class Movimentacao {
    var categoria: String? = null
    var descricao: String? = null
    var tipo: String? = null
    var valor = 0.0
    var key: String? = null
    fun setData() {}
    fun salvar(dataEscolhida: String?) {
        val autenticacao = firebaseAutenticacao
        val idUsuario = Objects.requireNonNull(Objects.requireNonNull(autenticacao!!.currentUser)?.email)?.let { codificarBase64(it) }
        val mesAno = mesAnoDataEscolhida(dataEscolhida!!)
        val firebase = firebaseDatabase
        if (idUsuario != null) {
            firebase!!.child("movimentacao")
                    .child(idUsuario)
                    .child(mesAno)
                    .push()
                    .setValue(this)
        }
    }
}