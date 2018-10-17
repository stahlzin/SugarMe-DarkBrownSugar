package br.com.mateus.sugarme.View;
//falta o delete, o download, o controller
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.DiarioGlicemicoDAO;
import br.com.mateus.sugarme.Model.Exame;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Utils.CoverterFactory.tryParseInt;

public class ExameActivity extends AppCompatActivity {

    private List<Exame> exameList = new ArrayList<>();
    private ExameActivity.ExameArrayAdapter exameArrayAdapter;
    private ListView exameListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DiarioGlicemico diarioGlicemico;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Exame);     //Titulo para ser exibido na sua Action Bar em frente à seta



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExameActivity.this, ExameAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        exameListView= (ListView) findViewById(R.id.exameListView);

        exameArrayAdapter = new ExameActivity.ExameArrayAdapter(this, exameList);
        exameListView.setAdapter(exameArrayAdapter);

        configuraObserverShortClick();
    }

    private void configuraObserverShortClick() {
        exameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dbuilder = new AlertDialog.Builder(ExameActivity.this);
                dbuilder.setPositiveButton(getString(R.string.downloadItemExame), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*DiarioGlicemico diarioGlicemico = exameListView.get(position);
                        DiarioGlicemicoDAO diarioGlicemicoDAO = new DiarioGlicemicoDAO();
                        diarioGlicemicoDAO.excluir(diarioGlicemico.getDiarioId());*/
                    }
                }).setNegativeButton(getString(R.string.deleteItemExame), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                dbuilder.show();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("exames").orderByChild("exameTimestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exameList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    Exame todos = json.getValue(Exame.class);
                    todos.setIdExame(json.getKey());
                    exameList.add(todos);
                }
                //inverte a lista
                Collections.reverse(exameList);
                exameArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    public static class ExameArrayAdapter extends ArrayAdapter<Exame> {
        public ExameArrayAdapter (Context context, List<Exame> forecast){
            super (context, -1, forecast);
        }

        private static class ViewHolder{
            TextView dataExameTextView;
            TextView descricaoExameTextView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Exame dgc = getItem (position);
            ExameActivity.ExameArrayAdapter.ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new ExameActivity.ExameArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_exame, parent, false);
                viewHolder.dataExameTextView = (TextView) convertView.findViewById(R.id.dataExameTextView);
                viewHolder.descricaoExameTextView = (TextView) convertView.findViewById(R.id.descricaoExameTextView);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ExameActivity.ExameArrayAdapter.ViewHolder)convertView.getTag();
            }

            Context context = getContext();
            viewHolder.dataExameTextView.setText(String.valueOf(dgc.getDataExame()));
            viewHolder.descricaoExameTextView.setText(dgc.getDescricaoExame());
            return convertView;
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(ExameActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ExameActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

}
