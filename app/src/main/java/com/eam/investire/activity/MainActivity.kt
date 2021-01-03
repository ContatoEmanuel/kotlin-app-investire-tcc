package com.eam.investire.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eam.investire.R
import com.eam.investire.config.ConfiguracaoFirebase
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide

class MainActivity : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isButtonBackVisible = false
        isButtonNextVisible = false
        addSlide(FragmentSlide.Builder()
                .background(R.color.colorGray)
                .fragment(R.layout.intro_1)
                .build())
        addSlide(FragmentSlide.Builder()
                .background(R.color.colorGray)
                .fragment(R.layout.intro_2)
                .build())
        addSlide(FragmentSlide.Builder()
                .background(R.color.colorGray)
                .fragment(R.layout.intro_3)
                .build())
        addSlide(FragmentSlide.Builder()
                .background(R.color.colorGray)
                .fragment(R.layout.intro_4)
                .build())
        addSlide(FragmentSlide.Builder()
                .background(R.color.colorGray)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build())
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    fun btEntrar(view: View?) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun btCadastrar(view: View?) {
        startActivity(Intent(this, CadastroActivity::class.java))
    }

    private fun verificarUsuarioLogado() {
        val autenticacao = ConfiguracaoFirebase.firebaseAutenticacao
        if (autenticacao!!.currentUser != null) {
            abrirTelaPrincipal()
        }
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(this, PrincipalActivity::class.java))
    }
}