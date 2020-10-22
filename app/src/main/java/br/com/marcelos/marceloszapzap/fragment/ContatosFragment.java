package br.com.marcelos.marceloszapzap.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.marcelos.marceloszapzap.R;
import br.com.marcelos.marceloszapzap.adapter.ContatosAdapter;
import br.com.marcelos.marceloszapzap.config.ConfiguracaoFirebase;
import br.com.marcelos.marceloszapzap.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference usuarioRef;
    //Evento da lista
    private ValueEventListener valueEventListener;

    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Configurações iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        //Configurar adapter
        adapter = new ContatosAdapter(listaContatos, getActivity());

        //Configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewListaContatos.setLayoutManager( layoutManager );
        recyclerViewListaContatos.setHasFixedSize( true );
        recyclerViewListaContatos.setAdapter( adapter );



        return view;
    }

    @Override
    public void onStart() {
        // cicle de vida do fragment
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Quando não utilizado, removemos o fragment
        usuarioRef.removeEventListener(valueEventListener);
    }

    public void recuperarContatos(){

        valueEventListener = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Percorrer a lista de usuarios vindos do banco
                for(DataSnapshot dados: dataSnapshot.getChildren()){

                    Usuario usuario = dados.getValue( Usuario.class );
                    listaContatos.add(usuario);
                }
                // Avisa que a lista foi modificada.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
