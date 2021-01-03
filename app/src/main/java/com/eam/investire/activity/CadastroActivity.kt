package com.eam.investire.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eam.investire.R
import com.eam.investire.config.ConfiguracaoFirebase
import com.eam.investire.helper.Base64Custom
import com.eam.investire.model.Usuario
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.util.*

class CadastroActivity : AppCompatActivity() {
    private var campoNome: EditText? = null
    private var campoEmail: EditText? = null
    private var campoSenha: EditText? = null
    private var usuario: Usuario? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        campoNome = findViewById(R.id.editNome)
        campoEmail = findViewById(R.id.editEmail)
        campoSenha = findViewById(R.id.editSenha)
        val botaoCadastrar = findViewById<Button>(R.id.buttonCadastrar)
        botaoCadastrar.setOnClickListener {
            val textoNome = campoNome!!.text.toString()
            val textoEmail = campoEmail!!.text.toString()
            val textoSenha = campoSenha!!.text.toString()
            if (textoNome.isNotEmpty()) {
                if (textoEmail.isNotEmpty()) {
                    if (textoSenha.isNotEmpty()) {
                        usuario = Usuario()
                        usuario!!.nome = textoNome
                        usuario!!.email = textoEmail
                        usuario!!.senha = textoSenha
                        cadastrarUsuario()
                    } else {
                        Toast.makeText(this@CadastroActivity,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CadastroActivity,
                            "Preencha o e-mail!",
                            Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@CadastroActivity,
                        "Preencha o nome!",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cadastrarUsuario() {
        val autenticacao = ConfiguracaoFirebase.firebaseAutenticacao
        usuario!!.email?.let {
            usuario!!.senha?.let { it1 ->
                autenticacao!!.createUserWithEmailAndPassword(
                        it,
                        it1
            ).addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val idUsuario = usuario!!.email?.let { it2 -> Base64Custom.codificarBase64(it2) }
                    usuario!!.setIdUsuario(idUsuario)
                    usuario!!.salvar()
                    finish()
                } else {
                    val excecao: String
                    try {
                        throw Objects.requireNonNull(task.exception)
                                ?: throw NullPointerException("Expression 'Objects.requireNonNull(task.exception)' must not be null")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        excecao = "Digite uma senha mais forte!"
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        excecao = "Digite um e-mail válido!"
                    } catch (e: FirebaseAuthUserCollisionException) {
                        excecao = "E-mail já cadastrado!"
                    } catch (e: Exception) {
                        excecao = "Erro ao cadastrar usuário" + e.message
                        e.printStackTrace()
                    }
                    Toast.makeText(this@CadastroActivity,
                            excecao,
                            Toast.LENGTH_SHORT).show()
                }
            }
            }
        }
    }
}