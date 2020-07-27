package br.com.marcelos.marceloszapzap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.com.marcelos.marceloszapzap.R;
import br.com.marcelos.marceloszapzap.config.ConfiguracaoFirebase;
import br.com.marcelos.marceloszapzap.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         //Recuperando a instancia do firebase
        autenticacao = ConfiguracaoFirebase.getFIrebaseAutenticacao();

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    public void validarAutenticacaoUsuario(View view){
        //Recuperando dados da tela de login
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        //Validação do login do usuário
        if( !textoEmail.isEmpty()){
            if( !textoSenha.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                // Autenticação do usuario
                logarUsuario( usuario );

            }else{
                Toast.makeText(LoginActivity.this,
                        "Preencha sua senha",
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(LoginActivity.this,
                    "Preencha o e-mail",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario(Usuario usuario){

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //Login efetuado com sucesso.
                    abrirTelaPrincipal();

                }else{

                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não cadastrado.";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não confere.";
                    } catch (Exception e){
                        excecao = "Erro na autenticação do usuário: "+e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Metodo para validar se o usuário já está logado no sistema
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        //
        if(usuarioAtual != null){
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view){

        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity( intent );
    }

    public void abrirTelaPrincipal(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity( intent );
    }


}
