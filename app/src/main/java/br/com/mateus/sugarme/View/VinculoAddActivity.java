package br.com.mateus.sugarme.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
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

import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;

public class VinculoAddActivity extends AppCompatActivity {

    private List<Medico> medicoList = new ArrayList<>();
    private MedicoArrayAdapter medicoArrayAdapter;
    private ListView medicoBuscaListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private TextInputEditText buscaMedicoInputEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinculo_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_VinculoAdd);     //Titulo para ser exibido na sua Action Bar em frente à seta

        medicoBuscaListView= (ListView) findViewById(R.id.medicoBuscaListView);
        buscaMedicoInputEditText = (TextInputEditText) findViewById(R.id.buscaMedicoInputEditText);



        medicoArrayAdapter = new MedicoArrayAdapter(this, medicoList);
        medicoBuscaListView.setAdapter(medicoArrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String crmBusca = buscaMedicoInputEditText.getText().toString();
                preencheMedicoListView(crmBusca);
            }
        });

        configuraObserverShortClick();
    }

    private void configuraObserverShortClick() {
        medicoBuscaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dbuilder = new AlertDialog.Builder(VinculoAddActivity.this);
                dbuilder.setTitle(R.string.vincularMedico);
                dbuilder.setPositiveButton(getString(R.string.simVincular), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Medico medico = medicoList.get(position);
                        setVinculosEntrePacienteEMedico(medico);
                        Toast.makeText(VinculoAddActivity.this, R.string.sucessoVinculo, Toast.LENGTH_SHORT).show();
                        FinishNavigation(VinculoAddActivity.this, VinculoActivity.class);
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

    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }

    private void setVinculosEntrePacienteEMedico(Medico medico) {
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("vinculos").child(medico.getIdMedico()).child("idMedico").setValue(medico.getIdMedico());
        databaseReference.child("users").child("medicos").child(medico.getIdMedico()).child("vinculos").child(userId).child("idPaciente").setValue(userId);
    }

    private void preencheMedicoListView(String crmBusca) {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("medicos").orderByChild("dados/crm").equalTo(crmBusca).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicoList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    Medico todos = json.child("dados").getValue(Medico.class);
                    todos.setIdMedico(json.getKey());
                    medicoList.add(todos);
                }
                if(medicoList.size()==0){
                    Toast.makeText(VinculoAddActivity.this, R.string.retornoBuscaMedicoVazio, Toast.LENGTH_LONG).show();
                    medicoArrayAdapter.notifyDataSetChanged();
                }else{
                    medicoArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class MedicoArrayAdapter extends ArrayAdapter<Medico> {
        public MedicoArrayAdapter(Context context, List<Medico> forecast) {
            super(context, -1, forecast);
        }

        private static class ViewHolder {
            TextView nomeMedicoTextView;
            TextView especialidadeMedicoTextView;
            TextView crmMedicoTextView;
            CircleImageView fotoMedicoCircleImageView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Medico dgc = getItem(position);
            final MedicoArrayAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new MedicoArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_busca_medico, parent, false);
                viewHolder.nomeMedicoTextView = (TextView) convertView.findViewById(R.id.nomeMedicoTextView);
                viewHolder.especialidadeMedicoTextView = (TextView) convertView.findViewById(R.id.especialidadeMedicoTextView);
                viewHolder.crmMedicoTextView = (TextView) convertView.findViewById(R.id.crmMedicoTextView);
                viewHolder.fotoMedicoCircleImageView = (CircleImageView) convertView.findViewById(R.id.fotoMedicoCircleImageView);
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
                    Glide.with(viewHolder.fotoMedicoCircleImageView.getContext()).load(urlFoto)
                            .into(viewHolder.fotoMedicoCircleImageView);
                }
            });

            return convertView;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                FinishNavigation(VinculoAddActivity.this, VinculoActivity.class);
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        FinishNavigation(VinculoAddActivity.this, VinculoActivity.class);
    }
}
