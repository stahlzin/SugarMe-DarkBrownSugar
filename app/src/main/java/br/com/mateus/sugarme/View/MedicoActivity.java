package br.com.mateus.sugarme.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.IdentityHashMap;

import br.com.mateus.sugarme.Controller.MedicoPresenter;
import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtra;
import static br.com.mateus.sugarme.Factory.NavigationFactory.SimpleNavigation;

public class MedicoActivity extends AppCompatActivity {
    private MedicoPresenter medicoPresenter;
    private GridLayout perfilMedicoGridLayout;
    private GridLayout qteChatGridLayout;
    private GridLayout chatMedicoGridLayout;
    private GridLayout pacientesMedicoGridLayout;
    private GridLayout medicoConfiguracoesGridLayout;
    private AlertDialog alerta;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private int chatFlag;


    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Id
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setTitle(R.string.app_name);
        medicoPresenter = new MedicoPresenter();

        perfilMedicoGridLayout = (GridLayout) findViewById(R.id.perfilMedicoGridLayout);
        chatMedicoGridLayout = (GridLayout) findViewById(R.id.chatMedicoGridLayout);
        pacientesMedicoGridLayout = (GridLayout) findViewById(R.id.pacientesMedicoGridLayout);
        qteChatGridLayout = (GridLayout) findViewById(R.id.qteChatGridLayout);
        medicoConfiguracoesGridLayout = (GridLayout) findViewById(R.id.medicoConfiguracoesGridLayout);
        final TextView qteSemLerChatTextView = (TextView) findViewById(R.id.qteSemLerChatTextView); //Qtde notificacoes
        final TextView qtdePacientesVinculadosTextView = (TextView) findViewById(R.id.qtePacientesVinculadosTextView);//Pacientes
        chatFlag = 1;

        //Configuração do Menu em GridLayout
       perfilMedicoGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditProfileType();


            }
        });

        chatMedicoGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chamar o chat
                if(chatFlag == 0){
                    AlertDialog.Builder dbuilder = new AlertDialog.Builder(MedicoActivity.this);
                    dbuilder.setTitle("Chat desabilitado");
                    dbuilder.setMessage("Deseja rever as opções de configuração?");
                    dbuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavigationWithOnePutExtra(MedicoActivity.this, ConfigurarActivity.class, "tipo", "medico");
                        }
                    }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dbuilder.show();
                }else{
                    SimpleNavigation(MedicoActivity.this, VinculoChatMedicoActivity.class);
                }

            }
        });


        qteChatGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleNavigation(MedicoActivity.this, VinculoChatMedicoActivity.class);
            }
        });

        pacientesMedicoGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Listar Pacientes, relatorio
                SimpleNavigation(MedicoActivity.this, PacientesVinculadosActivity.class);
            }
        });

        medicoConfiguracoesGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Configurações
                NavigationWithOnePutExtra(MedicoActivity.this, ConfigurarActivity.class, "tipo", "medico");
            }
        });
        //Fim da configuração do Menu


        //Ver conversas nao lidas
        databaseReference.child("users").child("medicos").child(userId).child("notificacoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    qteSemLerChatTextView.setText(Long.toString(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Ver pacientes vinculados
        databaseReference.child("users").child("medicos").child(userId).child("vinculos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    qtdePacientesVinculadosTextView.setText(Long.toString(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //alterar a view e o menu se não aceitar chat
        databaseReference.child("users").child("medicos").child(userId).child("configurar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("aceitaChat").getValue().equals("nao")){
                        qteChatGridLayout.setVisibility(View.INVISIBLE);
                        qteChatGridLayout.setVisibility(View.GONE);
                        chatFlag = 0;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.medico, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            NavigationWithOnePutExtra(MedicoActivity.this, ConfigurarActivity.class, "tipo", "medico");
            return true;
        }
        else if (id == R.id.action_about){
            NavigationWithOnePutExtra(MedicoActivity.this, SobreActivity.class, "tipo", "medico");
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setEditProfileType() {
        //Lista de itens
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Informações Cadastrais");
        itens.add("Foto");

        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_menu_perfil_medico, itens);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("O que deseja editar?");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                switch (arg1){
                    case 1: {
                        NavigationWithOnePutExtra(MedicoActivity.this, FotoPerfilActivity.class, "tipo", "medico");
                        break;
                    }
                    case 0:{
                        medicoPresenter.recebeMedico(MedicoActivity.this);
                        break;
                    }
                }
                alerta.dismiss();
            }
        });

        alerta = builder.create();
        alerta.show();
    }
}