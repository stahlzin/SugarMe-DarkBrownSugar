package br.com.mateus.sugarme.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import br.com.mateus.sugarme.DAO.PacienteDAO;
import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class VinculoActivity extends AppCompatActivity {

    private ListView medicoDisplayListView;
    private List<Medico> medicoList = new ArrayList<>();
    private List<Medico> todosMedicosList = new ArrayList<>();
    private List<String> idMedicosList = new ArrayList<>();
    private MedicoArrayAdapter medicoArrayAdapter;
    private ListView medicoBuscaListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceMedico;
    private ImageView orientacaoImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinculo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Vinculo);     //Titulo para ser exibido na sua Action Bar em frente à seta

        orientacaoImageView =(ImageView) findViewById(R.id.orientacaoImageView);
        orientacaoImageView.setVisibility(View.INVISIBLE);
        orientacaoImageView.setVisibility(View.GONE);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VinculoActivity.this, VinculoAddActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        medicoDisplayListView = (ListView) findViewById(R.id.medicoDisplayListView);
        medicoArrayAdapter = new MedicoArrayAdapter(this, medicoList);
        medicoDisplayListView.setAdapter(medicoArrayAdapter);

        //configuraObserverShortClick();

    }

    private void configuraObserverShortClick() {
        medicoDisplayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dbuilder = new AlertDialog.Builder(VinculoActivity.this);
                dbuilder.setTitle(R.string.desvincularMedico);
                dbuilder.setPositiveButton(getString(R.string.simVincular), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Medico medico = medicoList.get(position);
                        setDesvinculo(medico);
                        Toast.makeText(VinculoActivity.this, R.string.sucessoDesvinculo, Toast.LENGTH_SHORT).show();
                        getListaVinculos();
                    }
                }).setNegativeButton(getString(R.string.naoVincular), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                dbuilder.show();
            }
        });
    }

    private void setDesvinculo(Medico medico) {
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("medicos").child(medico.getIdMedico()).child("vinculos").child(userId).removeValue();
        databaseReference.child("users").child("pacientes").child(userId).child("vinculos").child(medico.getIdMedico()).removeValue();
    }

    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getUserId();
        getListaVinculos();
    }

    private void getListaVinculos (){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("vinculos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idMedicosList.clear();

                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    String id = (String) json.child("idMedico").getValue();
                    idMedicosList.add(id);
                }
                if(!idMedicosList.isEmpty()){
                    setListUptade();
                }else{
                    medicoList.clear();
                    medicoArrayAdapter.notifyDataSetChanged();
                    Toast.makeText(VinculoActivity.this, R.string.semVinculoMedico, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Medico
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
                            add.setTelefone("vinculo");
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

    //Medico
    public static class MedicoArrayAdapter extends ArrayAdapter<Medico> {
        public MedicoArrayAdapter(Context context, List<Medico> forecast) {
            super(context, -1, forecast);
        }

        private static class ViewHolder {
            TextView nomeMedicoTextView;
            TextView especialidadeMedicoTextView;
            TextView crmMedicoTextView;
            CircleImageView fotoMedicoCircleImageView;
            ImageView iconImageView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Medico dgc = getItem(position);
            final MedicoArrayAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new MedicoArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_busca_medico, parent, false);
                viewHolder.nomeMedicoTextView = (TextView) convertView.findViewById(R.id.nomeMedicoTextView);
                viewHolder.especialidadeMedicoTextView = (TextView) convertView.findViewById(R.id.especialidadeMedicoTextView);
                viewHolder.crmMedicoTextView = (TextView) convertView.findViewById(R.id.crmMedicoTextView);
                viewHolder.fotoMedicoCircleImageView = (CircleImageView) convertView.findViewById(R.id.fotoMedicoCircleImageView);
                viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MedicoArrayAdapter.ViewHolder) convertView.getTag();
            }

            final Context context = getContext();
            viewHolder.nomeMedicoTextView.setText(String.valueOf(dgc.getNome()));
            viewHolder.especialidadeMedicoTextView.setText(dgc.getEspecialidade());
            viewHolder.crmMedicoTextView.setText("CRM: " + dgc.getCrm());

            final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = firebaseStorage.getReference();
            storageRef.child("users").child("medicos").child(dgc.getIdMedico()).child("fotoPerfil/fotoPerfil.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    String urlFoto = uri.toString();
                    Glide.with(context).load(urlFoto)
                            .into(viewHolder.fotoMedicoCircleImageView);
                }
            });

            //colocando o icone de mensagem no Vinculo Chat Activity
            if(dgc.getTelefone().equals("mensagemNot")){
                viewHolder.iconImageView.setBackgroundResource(R.drawable.message_icon);
            }
            if(dgc.getTelefone().equals("vinculo")){
                viewHolder.iconImageView.setBackgroundResource(R.drawable.delete_icon);
                viewHolder.iconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dbuilder = new AlertDialog.Builder(context);
                        dbuilder.setTitle(R.string.desvincularMedico);
                        dbuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PacienteDAO pacienteDAO = new PacienteDAO();
                                pacienteDAO.setDesvinculo(dgc.getIdMedico());
                            }
                        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                        dbuilder.show();
                    }
                });

            }
            return convertView;

        }
    }

    //Paciente
    public static class PacienteArrayAdapter extends ArrayAdapter<Paciente> {
        public PacienteArrayAdapter(Context context, List<Paciente> forecast) {
            super(context, -1, forecast);
        }

        private static class ViewHolder {
            TextView nomePacienteTextView;
            TextView telefonePacienteTextView;
            CircleImageView fotoPacienteCircleImageView;
            ImageView iconImageView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Paciente dgc = getItem(position);
            final PacienteArrayAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new PacienteArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_busca_paciente, parent, false);
                viewHolder.nomePacienteTextView = (TextView) convertView.findViewById(R.id.nomePacienteTextView);
                viewHolder.telefonePacienteTextView = (TextView) convertView.findViewById(R.id.telefonePacienteTextView);
                viewHolder.fotoPacienteCircleImageView = (CircleImageView) convertView.findViewById(R.id.fotoPacienteCircleImageView);
                viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (PacienteArrayAdapter.ViewHolder) convertView.getTag();
            }

            final Context context = getContext();
            viewHolder.nomePacienteTextView.setText(String.valueOf(dgc.getNome()));
            viewHolder.telefonePacienteTextView.setText(dgc.getTelefone());

            final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = firebaseStorage.getReference();
            storageRef.child("users").child("pacientes").child(dgc.getIdPaciente()).child("fotoPerfil/fotoPerfil.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    String urlFoto = uri.toString();
                    Glide.with(context).load(urlFoto)
                            .into(viewHolder.fotoPacienteCircleImageView);

                }
            });

            if(dgc.getCpf().equals("mensagemNot")){
                viewHolder.iconImageView.setBackgroundResource(R.drawable.message_icon);
            }



            return convertView;

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(VinculoActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                VinculoActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

    private void setListUptadePaciente() {
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

}
