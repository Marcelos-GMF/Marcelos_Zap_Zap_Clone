package br.com.marcelos.marceloszapzap.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){

        if(Build.VERSION.SDK_INT >= 23){
            List<String> listaPermissoes = new ArrayList<>();

            //Percorrer as permissões passadas,
            // verificando uma a uma
            // caso já tenha permissão liberada
             for(String permissao : permissoes){
                 // Buscar e valida se já tem permissão
                 Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;

                 if( !temPermissao ){
                     listaPermissoes.add(permissao);
                 }
             }
            /**
             * Valida se caso a lista de permissão esteja vazia, não será necessario solicitar a permissão
             */
            if(listaPermissoes.isEmpty()){
                return true;
            }

            // Convertendo para um array de permissões
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            // Solicitar as permissões, obs.: o requestCod é usado para controlar de onde voce está chamando o metodo
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);

        }

        return true;
    }
}
