package br.com.marcelos.marceloszapzap.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.marcelos.marceloszapzap.R;
import br.com.marcelos.marceloszapzap.config.ConfiguracaoFirebase;
import br.com.marcelos.marceloszapzap.helper.Base64Custom;
import br.com.marcelos.marceloszapzap.helper.UsuarioFirebase;
import br.com.marcelos.marceloszapzap.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editPerfilNome);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    public void validarCadastroUsuario(View view){

        //Recuperando os textos que foram digitados na tela de cadastro
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if( !textoNome.isEmpty()){
            if( !textoEmail.isEmpty()){
                if(!textoSenha.isEmpty()){

                    //Chama o metodo para salvar usuario no firebase
                    Usuario usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    cadastrarUsuario( usuario );

                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a senha",
                            Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(CadastroActivity.this,
                        "Preencha o email",
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o nome",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarUsuario(final Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFIrebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
            usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    try{
                        // Salvando usuario no banco de dados firebase
                        String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(identificadorUsuario);
                        usuario.salvar();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    //Metodo para atualizar o nome do usuário perfil
                    UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                    finish();

                }else {
                    String excecao = "";
                    try{
                       throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um e-amil válido";
                    }catch(FirebaseAuthUserCollisionException e){
                        excecao = "Está conta já possui cadastro";
                    } catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "+e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
