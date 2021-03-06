package br.com.marcelos.marceloszapzap.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import br.com.marcelos.marceloszapzap.config.ConfiguracaoFirebase;
import br.com.marcelos.marceloszapzap.model.Usuario;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * @author Marcelos
 */

public class UsuarioFirebase {

    //Usuário logado
    public static String getIdentificadorUsuario() {

        FirebaseAuth usuario = ConfiguracaoFirebase.getFIrebaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;
    }

    // pega o usuário atual
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFIrebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarFotoUsuario(Uri url){

       try{
            FirebaseUser usuario = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();
            usuario.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil","Erro na atualização da foto do perfil do usuário.");
                    }
                }
            });

            return true;

       } catch (Exception e){
            e.printStackTrace();
            return false;
       }
    }

    public static boolean atualizarNomeUsuario(String nome){

        try{
            FirebaseUser usuario = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            usuario.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil","Erro ao atualizar nome do perfil do usuário.");
                    }
                }
            });

            return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser fireBaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(fireBaseUser.getEmail());
        usuario.setNome(fireBaseUser.getDisplayName());

        if(fireBaseUser.getPhotoUrl() == null){
          usuario.setFoto("");
        }else{
            usuario.setFoto(fireBaseUser.getPhotoUrl().toString());
        }

        return usuario;
    }
}
