package com.eam.investire.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eam.investire.R
import com.eam.investire.config.ConfiguracaoFirebase
import com.eam.investire.model.Usuario
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var campoEmail: EditText? = null
    private var campoSenha: EditText? = null
    private var usuario: Usuario? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        campoEmail = findViewById(R.id.editEmailLogin)
        campoSenha = findViewById(R.id.editSenhaLogin)
        val botaoEntrar = findViewById<Button>(R.id.buttonAcessar)
        botaoEntrar.setOnClickListener {
            val textoEmail = campoEmail!!.text.toString()
            val textoSenha = campoSenha!!.text.toString()
            if (textoEmail.isNotEmpty()) {
                if (textoSenha.isNotEmpty()) {
                    usuario = Usuario()
                    usuario!!.email = textoEmail
                    usuario!!.senha = textoSenha
                    validarLogin()
                } else {
                    Toast.makeText(this@LoginActivity,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@LoginActivity,
                        "Preencha o e-mail!",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarLogin() {
        val autenticacao = ConfiguracaoFirebase.firebaseAutenticacao
        usuario!!.email?.let {
            usuario!!.senha?.let { it1 ->
                autenticacao!!.signInWithEmailAndPassword(
                        it,
                        it1
            ).addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    abrirTelaPrincipal()
                } else {
                    val excecao: String
                    try {
                        throw Objects.requireNonNull(task.exception)
                                ?: throw NullPointerException("Expression 'Objects.requireNonNull(task.exception)' must not be null")
                    } catch (e: FirebaseAuthInvalidUserException) {
                        excecao = "Usuário não está cadastrado."
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        excecao = "E-mail e/ou senha não correspodem a um usuário cadastrado"
                    } catch (e: Exception) {
                        excecao = "Erro ao fazer login" + e.message
                        e.printStackTrace()
                    }
                    Toast.makeText(this@LoginActivity,
                            excecao,
                            Toast.LENGTH_SHORT).show()
                }
            }
            }
        }
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(this, PrincipalActivity::class.java))
        finish()
    }
}