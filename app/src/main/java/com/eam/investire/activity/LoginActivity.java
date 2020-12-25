package com.eam.investire.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eam.investire.R;
import com.eam.investire.config.ConfiguracaoFirebase;
import com.eam.investire.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmailLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);
        Button botaoEntrar = findViewById(R.id.buttonAcessar);

        botaoEntrar.setOnClickListener(v -> {

            String textoEmail = campoEmail.getText().toString();
            String textoSenha = campoSenha.getText().toString();

            if (!textoEmail.isEmpty()) {
                if (!textoSenha.isEmpty()) {
                    usuario = new Usuario();
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    validarLogin();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this,
                        "Preencha o e-mail!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void validarLogin() {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                abrirTelaPrincipal();
            } else {
                String excecao;
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    excecao = "Usuário não está cadastrado.";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    excecao = "E-mail e/ou senha não correspodem a um usuário cadastrado";
                } catch (Exception e) {
                    excecao = "Erro ao fazer login" + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(LoginActivity.this,
                        excecao,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void abrirTelaPrincipal() {
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}