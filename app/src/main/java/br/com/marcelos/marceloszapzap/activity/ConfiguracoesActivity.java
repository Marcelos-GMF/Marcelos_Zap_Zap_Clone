package br.com.marcelos.marceloszapzap.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import br.com.marcelos.marceloszapzap.R;
import br.com.marcelos.marceloszapzap.config.ConfiguracaoFirebase;
import br.com.marcelos.marceloszapzap.helper.Permissao;
import br.com.marcelos.marceloszapzap.helper.UsuarioFirebase;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.com.marcelos.marceloszapzap.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;

//@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ConfiguracoesActivity extends AppCompatActivity {

    private final String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView imageViewFotoPerfil;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private EditText editPerfilNome;
    private ImageView imageAtualizarNome;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Configurações iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        imageViewFotoPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        editPerfilNome = findViewById(R.id.editPerfilNome);
        imageAtualizarNome = findViewById(R.id.imageAtualizarNome);

        //Validar permissoes
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        // Criando um botão de voltar na tela
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recupera os dados do usuário Atual
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        // Validação caso não exista imagem, coloca a imagem padrão.
        if(url != null){
            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(imageViewFotoPerfil);
        }else {
            imageViewFotoPerfil.setImageResource(R.drawable.padrao);
        }
        // Colocando o nome novo de usuário
        editPerfilNome.setText(usuario.getDisplayName());

        //Evento de click
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 // Intent para solicitar funções padrões do Android, nesse caso a camera
                 // Envia mensagem para o android, para usar a função da camera
                 Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                 // Valida se o aparelho tem camera
                 if( i.resolveActivity(getPackageManager()) != null){

                     startActivityForResult(i, SELECAO_CAMERA);
                 }

            }
        });

        //EVENTO DE CLICK PARA INCLUIR UMA FOTO NO PERFIL DIRETO DA GALERIA
        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Intent para solicitar funções padrões do Android, nesse caso a galeria de fotos do aparelho
                // Envia mensagem para o android, para usar a galeria de fotos
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Valida se o aparelho tem camera
                if( i.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(i, SELECAO_GALERIA);
                }

            }
        });

        //EVENTO PARA ATUALIZAR O NOME DO PERFIL USUÁRIO
        imageAtualizarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editPerfilNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);

                if(retorno){

                    usuarioLogado.setNome(nome);
                    usuarioLogado.atualizar();

                    Toast.makeText(ConfiguracoesActivity.this,
                            "Nome alterado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK ){
            Bitmap imagem = null;

            try{

                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }
                if(imagem != null){

                    imageViewFotoPerfil.setImageBitmap(imagem);

                    //Recuperar os dados da imagem para o firebase
                    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayStream);
                    byte[] dadosImagem = byteArrayStream.toByteArray();

                    //Salvando imagem no banco Firebase Storage
                    final StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            //.child(identificadorUsuario)
                            .child(identificadorUsuario.concat(".jpeg"));

                    //Upload das imagens
                    UploadTask uploadTask = imageRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this,
                                    "Erro ao fazer upload da imagem para o servidor!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this,
                                    "Imagem enviada para o servidor com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            //Atualizando a imagem versão 11 do FireBase
                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizarFotoUsuario(url);


                            //Metodo atualizado para o FireBase > 11
                       /*     imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                   Uri url = task.getResult();
                                }
                            });*/

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    // Metodo para atualizar foto do usuário no firebase
    public void atualizarFotoUsuario(Uri url){
       boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);

       if(retorno){
           usuarioLogado.setFoto(url.toString());
           usuarioLogado.atualizar();

           Toast.makeText(ConfiguracoesActivity.this,
                   "Foto alterado com sucesso!",
                   Toast.LENGTH_SHORT).show();
       }

    }


    // Metodo para validar permissões no android caso o usuário negue a permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
              alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app será necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
