package br.com.mateus.sugarme.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.toByteArray;
import static br.com.mateus.sugarme.View.MainController.getUserId;

public class VinculoChatActivity extends AppCompatActivity {

    private ListView medicoDisplayListView;
    private List<Medico> medicoList = new ArrayList<>();
    private List<Medico> todosMedicosList = new ArrayList<>();
    private List<String> idMedicosList = new ArrayList<>();
    private List<String> notificacoesMedicosList = new ArrayList<>();
    private VinculoActivity.MedicoArrayAdapter medicoArrayAdapter;
    private ListView medicoBuscaListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceMedico;
    private ImageView orientacaoImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinculo_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_ChatVinculo);     //Titulo para ser exibido na sua Action Bar em frente à seta


        medicoDisplayListView = (ListView) findViewById(R.id.medicoDisplayListView);
        medicoArrayAdapter = new VinculoActivity.MedicoArrayAdapter(this, medicoList);
        medicoDisplayListView.setAdapter(medicoArrayAdapter);

        orientacaoImageView =(ImageView) findViewById(R.id.orientacaoImageView);

        orientacaoImageView.setVisibility(View.INVISIBLE);
        orientacaoImageView.setVisibility(View.GONE);

        configuraObserverShortClick();

    }

    private void configuraObserverShortClick() {
        medicoDisplayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Medico medico = medicoList.get(position);
                iniciarChat(medico);
            }
        });
    }

    private void iniciarChat(Medico medico) {
        Intent intent = new Intent(VinculoChatActivity.this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("chaveMedico", medico.getIdMedico());
        intent.putExtra("chavePaciente","0");
        VinculoChatActivity.this.startActivity(intent);
    }

    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getUserId();
        getListaNotificacoes();
        getListaVinculos();
    }

    private void getListaNotificacoes() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("notificacoes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot json : dataSnapshot.getChildren()){
                    notificacoesMedicosList.add(json.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getListaVinculos (){

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("vinculos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idMedicosList.clear();

                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    String id = (String) json.child("idMedico").getValue();
                    seAceitaChat(id);
                }
                if(!idMedicosList.isEmpty()){
                    setListUptade();
                }/*else{
                    medicoList.clear();
                    medicoArrayAdapter.notifyDataSetChanged();
                    Toast.makeText(VinculoChatActivity.this, R.string.semVinculoChatMedico, Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seAceitaChat(final String id) {
        final boolean aceita;
        databaseReference.child("users").child("medicos").child(id).child("configurar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotMedico) {
                if(dataSnapshotMedico.exists()){
                    if(dataSnapshotMedico.child("aceitaChat").getValue().toString().equals("sim")){
                        idMedicosList.add(id);
                        setListUptade();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }

    private void setListUptade() {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("medicos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicoList.clear();
                todosMedicosList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    Medico todos = json.child("dados").getValue(Medico.class);
                    todos.setIdMedico(json.getKey());
                    todosMedicosList.add(todos);
                }
                for (int i = 0; i < todosMedicosList.size(); i++){
                    for (int j = 0; j < idMedicosList.size(); j++){
                        if(todosMedicosList.get(i).getIdMedico().equals(idMedicosList.get(j))){
                            Medico add = todosMedicosList.get(i);
                            if(notificacoesMedicosList.contains(add.getIdMedico())){
                                add.setTelefone("mensagemNot");
                            }
                            medicoList.add(add);
                        }
                    }
                }


                medicoArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(VinculoChatActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                VinculoChatActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

}