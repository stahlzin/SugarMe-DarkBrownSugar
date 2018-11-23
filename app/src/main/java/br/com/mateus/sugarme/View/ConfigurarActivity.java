package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.mateus.sugarme.Controller.MedicoPresenter;
import br.com.mateus.sugarme.Controller.PacientePresenter;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Controller.ConfigurarController.alterarCofigurarMedico;
import static br.com.mateus.sugarme.Controller.ConfigurarController.alterarCofigurarPaciente;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.View.MainController.getUserId;

public class ConfigurarActivity extends AppCompatActivity {

    /***
     * A classe ConfigurarActivity atua nos usuários Paciente e Médico, adaptada para cada um.
     * Recebe um parametro para identificar se é paciente ou médico
     * Utiliza um Controller para inserir e alterar as configurações.
     * Utiliza um Observer, programado em listener no onStart para trazer as configurações prévias
     * Utiliza o NavigationFactory para a navegação
     */
    private PacientePresenter pacientePresenter;
    private MedicoPresenter medicoPresenter;
    private Switch aceitaChatswitch;
    private Switch compartilhaDiarioSwitch;
    private EditText padHipoEditText;
    private EditText padHiperEditText;
    private GridLayout standardValuesGridLayout;
    private TextView standardValuesTextView;
    private GridLayout compartilhaDiarioGridLayout;
    private Button logoutButton;
    private Button salvarAlterButton;
    private String tipoUsuario;
    private String userId;
    private String aceitaChat;
    private String compartilhaDiario;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Configuracoes);

        pacientePresenter = new PacientePresenter();
        medicoPresenter = new MedicoPresenter();

        aceitaChatswitch = (Switch) findViewById(R.id.aceitaChatswitch);
        compartilhaDiarioSwitch = (Switch) findViewById(R.id.compartilhaDiarioSwitch);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        salvarAlterButton = (Button) findViewById(R.id.salvarAlterButton);
        standardValuesGridLayout = (GridLayout) findViewById(R.id.standardValuesGridLayout);
        standardValuesTextView = (TextView) findViewById(R.id.standardValuesTextView);
        compartilhaDiarioGridLayout = (GridLayout) findViewById(R.id.compartilhaDiarioGridLayout);
        padHipoEditText = (EditText) findViewById(R.id.padHipoEditText);
        padHiperEditText = (EditText) findViewById(R.id.padHiperEditText);



        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("tipo").equals("paciente")) {
                tipoUsuario = "paciente";
            }
            else if(it.getStringExtra("tipo").equals("medico")){
                tipoUsuario = "medico";
                standardValuesGridLayout.setVisibility(View.INVISIBLE);
                standardValuesGridLayout.setVisibility(View.GONE);
                standardValuesTextView.setVisibility(View.INVISIBLE);
                standardValuesTextView.setVisibility(View.GONE);
                compartilhaDiarioGridLayout.setVisibility(View.INVISIBLE);
                compartilhaDiarioGridLayout.setVisibility(View.GONE);
            }
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipoUsuario.equals("paciente")){
                    pacientePresenter.logout();
                    FinishNavigation(ConfigurarActivity.this, MainActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    medicoPresenter.logout();
                    FinishNavigation(ConfigurarActivity.this, MainActivity.class);
                }
            }
        });

        aceitaChatswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    aceitaChat = "sim";
                } else {
                    aceitaChat = "nao";
                }
            }
        });

        compartilhaDiarioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    compartilhaDiario = "sim";
                } else {
                    compartilhaDiario = "nao";
                }
            }
        });

        salvarAlterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = getUserId();
                if(tipoUsuario.equals("paciente")) {
                    alterarCofigurarPaciente(userId, aceitaChat, compartilhaDiario, padHipoEditText.getText().toString(), padHiperEditText.getText().toString());
                    Toast.makeText(ConfigurarActivity.this, R.string.alterSalva, Toast.LENGTH_SHORT).show();
                    FinishNavigation(ConfigurarActivity.this, PacienteActivity.class);
                }
                else if(tipoUsuario.equals("medico")){
                    alterarCofigurarMedico(userId, aceitaChat);
                    Toast.makeText(ConfigurarActivity.this, R.string.alterSalva, Toast.LENGTH_SHORT).show();
                    FinishNavigation(ConfigurarActivity.this, MedicoActivity.class);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userId = getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if(tipoUsuario.equals("paciente")) {
           databaseReference.child("users").child("pacientes").child(userId).child("configurar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if (dataSnapshot.child("aceitaChat").getValue() == "nao") {
                            aceitaChatswitch.setChecked(false);
                        } else {
                            aceitaChatswitch.setChecked(true);
                        }

                        if (dataSnapshot.child("compartilharDiario").getValue() == "nao") {
                            compartilhaDiarioSwitch.setChecked(false);
                        } else {
                            compartilhaDiarioSwitch.setChecked(true);
                        }

                        if(dataSnapshot.child("hipoglicemiaPadrao").getValue() == null){
                            padHipoEditText.setText(" ");
                        }else{
                            padHipoEditText.setText(String.valueOf(dataSnapshot.child("hipoglicemiaPadrao").getValue()));
                        }


                        if(dataSnapshot.child("hiperglicemiaPadrao").getValue() == null){
                            padHiperEditText.setText(" ");
                        } else{
                            padHiperEditText.setText(String.valueOf(dataSnapshot.child("hiperglicemiaPadrao").getValue()));
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(tipoUsuario.equals("medico")){
            databaseReference.child("users").child("medicos").child(userId).child("configurar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if (dataSnapshot.child("aceitaChat").getValue().equals("nao")) {
                            aceitaChatswitch.setChecked(false);
                        } else {
                            aceitaChatswitch.setChecked(true);
                        }

                        }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                if(tipoUsuario.equals("paciente")){
                    FinishNavigation(ConfigurarActivity.this, PacienteActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    FinishNavigation(ConfigurarActivity.this, MedicoActivity.class);
                }
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(tipoUsuario.equals("paciente")){
            FinishNavigation(ConfigurarActivity.this, PacienteActivity.class);
        }else if (tipoUsuario.equals("medico")){
            FinishNavigation(ConfigurarActivity.this, MedicoActivity.class);
        }
    }
}
