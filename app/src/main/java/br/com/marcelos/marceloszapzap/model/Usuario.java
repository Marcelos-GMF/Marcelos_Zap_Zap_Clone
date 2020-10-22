package br.com.marcelos.marceloszapzap.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.com.marcelos.marceloszapzap.config.ConfiguracaoFirebase;
import br.com.marcelos.marceloszapzap.helper.UsuarioFirebase;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child(getId());

        usuario.setValue( this );
    }

    public void atualizar(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = database.child("usuarios")
                .child(identificadorUsuario);

        // Convetendo o objeto em um Map
        Map<String, Object> valoresUsuario = conventerParaMap();
        //Atualiza os dados do usuário.
        usuarioRef.updateChildren( valoresUsuario );
    }

    @Exclude
    public Map<String, Object> conventerParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

    @Exclude //Excluir o usuário ao inserir no banco de dados
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
