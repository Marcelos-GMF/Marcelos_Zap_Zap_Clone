package br.com.marcelos.marceloszapzap.helper;

import br.com.marcelos.marceloszapzap.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

/**
 * @author Marcelos
 */

public class UsuarioFirebase {

    //Usuário logado
    public static String getIdentificadroUsuario() {

        FirebaseAuth usuario = ConfiguracaoFirebase.getFIrebaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;
    }

    // pega o usuário atual


}
