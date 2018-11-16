package br.com.mateus.sugarme.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.R;

public class VinculoChatMedicoActivity extends AppCompatActivity {
    private ListView medicoDisplayListView; //Neste caso mantive o nome de "Medico" mas será carregado com dados do Paciente
    private List<Paciente> pacienteList = new ArrayList<>();
    private List<Paciente> todosPacientesList = new ArrayList<>();
    private List<String> idPacientesList = new ArrayList<>();
    private List<String> notificacoesPacientesList = new ArrayList<>();
    private VinculoActivity.PacienteArrayAdapter pacienteArrayAdapter;
    private ListView pacienteBuscaListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceMedico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinculo_chat_medico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Pacientes);     //Titulo para ser exibido na sua Action Bar em frente à seta


        medicoDisplayListView = (ListView) findViewById(R.id.medicoDisplayListView);
        pacienteArrayAdapter = new VinculoActivity.PacienteArrayAdapter(this, pacienteList);
        medicoDisplayListView.setAdapter(pacienteArrayAdapter);

        configuraObserverShortClick();

    }

    private void configuraObserverShortClick() {
        medicoDisplayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Paciente paciente = pacienteList.get(position);
                iniciarChat(paciente);
            }
        });
    }

    private void iniciarChat(Paciente paciente) {
        Intent intent = new Intent(VinculoChatMedicoActivity.this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("chaveMedico", "0");
        intent.putExtra("chavePaciente",paciente.getIdPaciente());
        VinculoChatMedicoActivity.this.startActivity(intent);
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
        databaseReference.child("users").child("medicos").child(userId).child("notificacoes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot json : dataSnapshot.getChildren()){
                    notificacoesPacientesList.add(json.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getListaVinculos (){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("medicos").child(userId).child("vinculos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idPacientesList.clear();

                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    String id = (String) json.child("idPaciente").getValue();
                    seAceitaChat(id);
                }
                if(!idPacientesList.isEmpty()){
                    setListUptade();
                }/*else{
                    pacienteList.clear();
                    pacienteArrayAdapter.notifyDataSetChanged();
                    Toast.makeText(VinculoChatMedicoActivity.this, R.string.semVinculoChatPaciente, Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seAceitaChat(final String id) {
        databaseReference.child("users").child("pacientes").child(id).child("configurar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotPaciente) {
                if(dataSnapshotPaciente.exists()){
                    if(dataSnapshotPaciente.child("aceitaChat").getValue().toString().equals("sim")){
                        idPacientesList.add(id);
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

        databaseReference.child("users").child("pacientes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pacienteList.clear();
                todosPacientesList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    Paciente todos = json.child("dados").getValue(Paciente.class);
                    todos.setIdPaciente(json.getKey());
                    todosPacientesList.add(todos);
                }

                for (int i = 0; i < todosPacientesList.size(); i++){
                    for (int j = 0; j < idPacientesList.size(); j++){
                        if(todosPacientesList.get(i).getIdPaciente().equals(idPacientesList.get(j))){
                            Paciente add = todosPacientesList.get(i);
                            if(notificacoesPacientesList.contains(idPacientesList.get(j))){
                                add.setNome(add.getNome() + " -> Nova(s) mensagens!");
                            }
                            pacienteList.add(add);
                        }
                    }
                }
                pacienteArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class PacienteArrayAdapter extends ArrayAdapter<Paciente> {
        public PacienteArrayAdapter(Context context, List<Paciente> forecast) {
            super(context, -1, forecast);
        }


        private static class ViewHolder {
            TextView nomePacienteTextView;
            TextView telefonePacienteTextView;
            TextView cpfPacienteTextView;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Paciente dgc = getItem(position);
            VinculoChatMedicoActivity.PacienteArrayAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new VinculoChatMedicoActivity.PacienteArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_busca_paciente, parent, false);
                viewHolder.nomePacienteTextView = (TextView) convertView.findViewById(R.id.nomePacienteTextView);
                viewHolder.telefonePacienteTextView = (TextView) convertView.findViewById(R.id.telefonePacienteTextView);
                viewHolder.cpfPacienteTextView = (TextView) convertView.findViewById(R.id.cpfPacienteTextView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (VinculoChatMedicoActivity.PacienteArrayAdapter.ViewHolder) convertView.getTag();
            }

            Context context = getContext();
            viewHolder.nomePacienteTextView.setText(String.valueOf(dgc.getNome()));
            viewHolder.telefonePacienteTextView.setText(dgc.getTelefone());
            viewHolder.cpfPacienteTextView.setText(dgc.getCpf());
            return convertView;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(VinculoChatMedicoActivity.this, MedicoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                VinculoChatMedicoActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }
}
