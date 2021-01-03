package com.eam.investire.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eam.investire.R
import com.eam.investire.config.ConfiguracaoFirebase
import com.eam.investire.helper.Base64Custom
import com.eam.investire.helper.DateCustom
import com.eam.investire.model.Movimentacao
import com.eam.investire.model.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class ReceitasActivity : AppCompatActivity() {
    private var campoData: TextInputEditText? = null
    private var campoCategoria: TextInputEditText? = null
    private var campoDescricao: TextInputEditText? = null
    private var campoValor: EditText? = null
    private val firebaseRef = ConfiguracaoFirebase.firebaseDatabase
    private val autenticacao = ConfiguracaoFirebase.firebaseAutenticacao
    private var receitaTotal = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receitas)
        campoData = findViewById(R.id.editData)
        campoCategoria = findViewById(R.id.editCategoria)
        campoDescricao = findViewById(R.id.editDescricao)
        campoValor = findViewById(R.id.editValor)
        campoData!!.setText(DateCustom.dataAtual())
        recuperarReceitaTotal()
    }

    fun salvarReceita(view: View?) {
        if (validarCamposReceita()) {
            val movimentacao = Movimentacao()
            val data = Objects.requireNonNull(campoData!!.text).toString()
            val valorRecuperado = campoValor!!.text.toString().toDouble()
            movimentacao.valor = valorRecuperado
            movimentacao.categoria = Objects.requireNonNull(campoCategoria!!.text).toString()
            movimentacao.descricao = Objects.requireNonNull(campoDescricao!!.text).toString()
            movimentacao.setData()
            movimentacao.tipo = "r"
            val receitaAtualizada = receitaTotal + valorRecuperado
            atualizarReceita(receitaAtualizada)
            movimentacao.salvar(data)
            finish()
        }
    }

    private fun validarCamposReceita(): Boolean {
        val textoValor = campoValor!!.text.toString()
        val textoData = Objects.requireNonNull(campoData!!.text).toString()
        val textoCategoria = Objects.requireNonNull(campoCategoria!!.text).toString()
        val textoDescricao = Objects.requireNonNull(campoDescricao!!.text).toString()
        return if (textoValor.isNotEmpty()) {
            if (textoData.isNotEmpty()) {
                if (textoCategoria.isNotEmpty()) {
                    if (textoDescricao.isNotEmpty()) {
                        true
                    } else {
                        Toast.makeText(this@ReceitasActivity,
                                "Descrição não foi preenchida!",
                                Toast.LENGTH_SHORT).show()
                        false
                    }
                } else {
                    Toast.makeText(this@ReceitasActivity,
                            "Categoria não foi preenchida!",
                            Toast.LENGTH_SHORT).show()
                    false
                }
            } else {
                Toast.makeText(this@ReceitasActivity,
                        "Data não foi preenchida!",
                        Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(this@ReceitasActivity,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun recuperarReceitaTotal() {
        val emailUsuario = Objects.requireNonNull(autenticacao!!.currentUser)?.email!!
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        val usuarioRef = firebaseRef!!.child("usuarios").child(idUsuario)
        usuarioRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usuario = dataSnapshot.getValue(Usuario::class.java)!!
                receitaTotal = usuario.receitaTotal
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun atualizarReceita(receita: Double) {
        val emailUsuario = Objects.requireNonNull(autenticacao!!.currentUser)?.email!!
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        val usuarioRef = firebaseRef!!.child("usuarios").child(idUsuario)
        usuarioRef.child("receitaTotal").setValue(receita)
    }
}