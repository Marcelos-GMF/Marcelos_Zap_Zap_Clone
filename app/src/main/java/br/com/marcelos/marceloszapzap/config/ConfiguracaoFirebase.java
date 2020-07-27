package br.com.marcelos.marceloszapzap.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;

    // Retorna a instacia do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){
       if( database == null){
           database = FirebaseDatabase.getInstance().getReference();
       }
       return database;
    }

    //Retorna a instancia do FirebaseAuth, para validação do usuario
    public static FirebaseAuth getFIrebaseAutenticacao(){
        if ( auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
