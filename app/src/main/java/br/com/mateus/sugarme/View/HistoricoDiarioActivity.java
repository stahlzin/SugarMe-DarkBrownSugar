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
import br.com.mateus.sugarme.DAO.DiarioGlicemicoDAO;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseInt;

public class HistoricoDiarioActivity extends AppCompatActivity {

    private List<DiarioGlicemico> diarioGlicemicoList = new ArrayList<>();
    private HistoricoDiarioArrayAdapter historicoDiarioArrayAdapter;
    private ListView diarioHistoricoListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DiarioGlicemico diarioGlicemico;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_diario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Historico_Diario);     //Titulo para ser exibido na sua Action Bar em frente à seta

        diarioHistoricoListView= (ListView) findViewById(R.id.diarioHistoricoListView);

        historicoDiarioArrayAdapter = new HistoricoDiarioArrayAdapter(this, diarioGlicemicoList);
        diarioHistoricoListView.setAdapter(historicoDiarioArrayAdapter);

        configuraObserverShortClick();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("diario").orderByChild("gliTimestamp").limitToLast(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                diarioGlicemicoList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    DiarioGlicemico todos = json.getValue(DiarioGlicemico.class);
                    todos.setDiarioId(json.getKey());
                    diarioGlicemicoList.add(todos);
                }
                //inverte a lista
                Collections.reverse(diarioGlicemicoList);
                historicoDiarioArrayAdapter.notifyDataSetChanged();
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


    public static class HistoricoDiarioArrayAdapter extends ArrayAdapter<DiarioGlicemico> {
        public HistoricoDiarioArrayAdapter (Context context, List<DiarioGlicemico> forecast){
            super (context, -1, forecast);
        }

        private static class ViewHolder{
            TextView glicemiaTextView;
            TextView categoriaTextView;
            TextView dataTextView;
            TextView horaTextView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DiarioGlicemico dgc = getItem (position);
            ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_diario_historico, parent, false);
                viewHolder.glicemiaTextView = (TextView) convertView.findViewById(R.id.glicemiaTextView);
                viewHolder.categoriaTextView = (TextView) convertView.findViewById(R.id.categoriaTextView);
                viewHolder.dataTextView = (TextView) convertView.findViewById(R.id.dataTextView);
                viewHolder.horaTextView = (TextView) convertView.findViewById(R.id.horaTextView);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            Context context = getContext();
            viewHolder.glicemiaTextView.setText(String.valueOf(dgc.getGlicemia()));
            int valor = tryParseInt(viewHolder.glicemiaTextView.getText().toString());
            int color = changeColor(valor);
            switch (color){
                case -2:
                    viewHolder.glicemiaTextView.setBackgroundResource(R.color.colorHiper);
                    break;
                case -1:
                    viewHolder.glicemiaTextView.setBackgroundResource(R.color.colorHipo);
                    break;
                case 0:
                    viewHolder.glicemiaTextView.setBackgroundResource(R.color.colorNormal);
                    break;
            }


            viewHolder.categoriaTextView.setText(dgc.getCategoria());
            viewHolder.dataTextView.setText(dgc.getData());
            viewHolder.horaTextView.setText(dgc.getHora());
            return convertView;

        }

    }

    private static int changeColor(int valor) {
        if (valor <= 70) {
            return -1;
        }
        if (valor >= 200) {
            return -2;
        }
        return 0;
    }

    private void configuraObserverShortClick(){
        diarioHistoricoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dbuilder = new AlertDialog.Builder(HistoricoDiarioActivity.this);
                dbuilder.setPositiveButton(getString(R.string.deletarItemHistorico), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DiarioGlicemico diarioGlicemico = diarioGlicemicoList.get(position);
                        DiarioGlicemicoDAO diarioGlicemicoDAO = new DiarioGlicemicoDAO();
                        diarioGlicemicoDAO.excluir(diarioGlicemico.getDiarioId());
                    }
                }).create();
                dbuilder.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(HistoricoDiarioActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                HistoricoDiarioActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }



}
