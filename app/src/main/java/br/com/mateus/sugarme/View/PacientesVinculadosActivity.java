package br.com.mateus.sugarme.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Singleton.UserSingleton;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.toBitmap;
import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtraAndUserId;

public class PacientesVinculadosActivity extends AppCompatActivity {
    private ListView medicoDisplayListView; //Neste caso mantive o nome de "Medico" mas será carregado com dados do Paciente
    private List<Paciente> pacienteList = new ArrayList<>();
    private List<Paciente> todosPacientesList = new ArrayList<>();
    private List<String> idPacientesList = new ArrayList<>();
    private PacientesVinculadosActivity.PacienteArrayAdapter pacienteArrayAdapter;
    private ListView pacienteBuscaListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private static String imageUrl;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceMedico;
    private static Map<String, Bitmap> bitmaps = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes_vinculados);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Pacientes);     //Titulo para ser exibido na sua Action Bar em frente à seta


        medicoDisplayListView = (ListView) findViewById(R.id.medicoDisplayListView);
        pacienteArrayAdapter = new PacientesVinculadosActivity.PacienteArrayAdapter(this, pacienteList);
        medicoDisplayListView.setAdapter(pacienteArrayAdapter);


        medicoDisplayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Paciente paciente = pacienteList.get(position);
                //consulta se o paciente aceita compartilhar o diário e redireciona
                databaseReference.child("users").child("pacientes").child(pacienteList.get(position).getIdPaciente()).child("configurar").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.child("compartilharDiario").getValue().equals("nao")){
                                Toast.makeText(PacientesVinculadosActivity.this, "O paciente selecionado optou por não compartilhar o diário glicêmico", Toast.LENGTH_LONG).show();
                            }else{
                                NavigationWithOnePutExtraAndUserId(PacientesVinculadosActivity.this, RelatorioActivity.class, "tipo", "medico", "userId", paciente.getIdPaciente());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


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

        databaseReference.child("users").child("medicos").child(userId).child("vinculos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idPacientesList.clear();

                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    String id = (String) json.child("idPaciente").getValue();
                    idPacientesList.add(id);
                }
                if(!idPacientesList.isEmpty()){
                    setListUptade();
                }
                else{
                    pacienteList.clear();
                    pacienteArrayAdapter.notifyDataSetChanged();
                    Toast.makeText(PacientesVinculadosActivity.this, R.string.semVinculoPaciente, Toast.LENGTH_SHORT).show();
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
            CircleImageView fotoPacienteCircleImageView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Paciente dgc = getItem(position);
            final PacientesVinculadosActivity.PacienteArrayAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new PacientesVinculadosActivity.PacienteArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_busca_paciente, parent, false);
                viewHolder.nomePacienteTextView = (TextView) convertView.findViewById(R.id.nomePacienteTextView);
                viewHolder.telefonePacienteTextView = (TextView) convertView.findViewById(R.id.telefonePacienteTextView);
                viewHolder.fotoPacienteCircleImageView = (CircleImageView) convertView.findViewById(R.id.fotoPacienteCircleImageView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (PacientesVinculadosActivity.PacienteArrayAdapter.ViewHolder) convertView.getTag();
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
            return convertView;

        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                FinishNavigation(PacientesVinculadosActivity.this, MedicoActivity.class);
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        FinishNavigation(PacientesVinculadosActivity.this, MedicoActivity.class);
    }
}
