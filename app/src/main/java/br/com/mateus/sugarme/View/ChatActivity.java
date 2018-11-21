package br.com.mateus.sugarme.View;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseIndexListAdapter;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.mateus.sugarme.Model.ChatMessage;
import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;


public class ChatActivity extends AppCompatActivity {

    private String chaveMedico;
    private String chavePaciente;
    private String activityQueChamou;
    private int size = 0;

    //FirebaseListAdapter
    com.firebase.ui.database.FirebaseListAdapter<ChatMessage> adapter;

    //FindViewByID
    private  FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_ChatVinculo);     //Titulo para ser exibido na sua Action Bar em frente à seta



        fab = (FloatingActionButton)findViewById(R.id.fabChat);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference().child("users").child("pacientes").child(chavePaciente).child("chats").child(chaveMedico)
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                //notifica
                notifica();


                // Clear the input
                input.setText("");
            }
        });

        //Parametros do PutExtra
        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("chavePaciente").toString().equals("0")){
                   activityQueChamou = "pacientes";
                   chavePaciente = getUserId();
                   chaveMedico = it.getStringExtra("chaveMedico").toString();
            }
            else{
                activityQueChamou = "medicos";
                chaveMedico = getUserId();
                chavePaciente = it.getStringExtra("chavePaciente").toString();
            }
        }
        /*
        //OnDataUpdated - Assim soma-se as mensagens existentes
        FirebaseDatabase.getInstance()
                .getReference().child("users").child("pacientes").child(chavePaciente).child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot percorre:dataSnapshot.getChildren()){
                    size += percorre.getChildrenCount();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */

        //Mostrar mensagens
        displayChatMessages();


    }//Fim do OnCreate

    private void notifica() {
        if(activityQueChamou.equals("medicos")){
            //Notificar paciente
            FirebaseDatabase.getInstance()
                    .getReference().child("users").child("pacientes").child(chavePaciente).
                    child("notificacoes").child(chaveMedico).setValue(1);
        }
        else{
            //Notificar medico
            FirebaseDatabase.getInstance()
                    .getReference().child("users").child("medicos").child(chaveMedico).
                    child("notificacoes").child(chavePaciente).setValue(1);
        }
    }

    //Pegar Id Usuario
    private String getUserId(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser().getUid();
    }

    //Display
    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("users")
                .child("pacientes").child(chavePaciente).child("chats").child(chaveMedico)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }


    private void removeNotificacao(){
        if(activityQueChamou.equals("pacientes")){ //Paciente

            //Tirar notificacao
            FirebaseDatabase.getInstance()
                    .getReference().child("users").child("pacientes").child(chavePaciente).
                    child("notificacoes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(chaveMedico).exists()){
                        FirebaseDatabase.getInstance()
                                .getReference().child("users").child("pacientes").child(chavePaciente).
                                child("notificacoes").child(chaveMedico).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Intent intent = new Intent(ChatActivity.this, PacienteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ChatActivity.this.startActivity(intent);
        }

        //Medico
        else{
            //Tirar notificacao
            FirebaseDatabase.getInstance()
                    .getReference().child("users").child("medicos").child(chaveMedico).
                    child("notificacoes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(chavePaciente).exists()){
                        FirebaseDatabase.getInstance()
                                .getReference().child("users").child("medicos").child(chaveMedico).
                                child("notificacoes").child(chavePaciente).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Intent intent = new Intent(ChatActivity.this, MedicoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ChatActivity.this.startActivity(intent);
        }
    }

    //onBackPressed
    @Override
    public void onBackPressed() {

        removeNotificacao();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                removeNotificacao();
                break;
            default:break;
        }
        return true;
    }
}

