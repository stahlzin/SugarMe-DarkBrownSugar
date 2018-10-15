package br.com.mateus.sugarme.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import br.com.mateus.sugarme.Model.Intercorrencia;
import br.com.mateus.sugarme.Model.IntercorrenciaDAO;
import br.com.mateus.sugarme.R;

public class IntercorrenciaActivity extends AppCompatActivity {

    private List<Intercorrencia> intercorrenciaList = new ArrayList<>();
    private IntercorrenciaActivity.IntercorrenciaArrayAdapter intercorrenciaArrayAdapter;
    private ListView intercorrenciaListView;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercorrencia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Intercorrencia);     //Titulo para ser exibido na sua Action Bar em frente à seta

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntercorrenciaActivity.this, IntercorrenciaAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        intercorrenciaListView= (ListView) findViewById(R.id.intercorrenciaListView);

        intercorrenciaArrayAdapter = new IntercorrenciaActivity.IntercorrenciaArrayAdapter(this, intercorrenciaList);
        intercorrenciaListView.setAdapter(intercorrenciaArrayAdapter);

        configuraObserverShortClick();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("Intercorrencias").orderByChild("interTimestamp").limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                intercorrenciaList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    Intercorrencia todos = json.getValue(Intercorrencia.class);
                    todos.setId(json.getKey());
                    intercorrenciaList.add(todos);
                }
                //inverte a lista
                Collections.reverse(intercorrenciaList);
                intercorrenciaArrayAdapter.notifyDataSetChanged();
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


    public static class IntercorrenciaArrayAdapter extends ArrayAdapter<Intercorrencia> {
        public IntercorrenciaArrayAdapter (Context context, List<Intercorrencia> forecast){
            super (context, -1, forecast);
        }

        private static class ViewHolder {
            TextView dataInterTextView;
            TextView horaInterTextView;
            TextView hiperInterTextView;
            TextView hipoInterTextView;
            TextView sedeInterTextView;
            TextView nauseaInterTextView;
            TextView desmaioInterTextView;
            TextView internacaoInterTextView;
            TextView anotacoesInterTextView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Intercorrencia dgc = getItem (position);
            IntercorrenciaActivity.IntercorrenciaArrayAdapter.ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new IntercorrenciaActivity.IntercorrenciaArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_item_intercorrencia, parent, false);

                viewHolder.dataInterTextView = (TextView) convertView.findViewById(R.id.dataInterTextView);
                viewHolder.horaInterTextView = (TextView) convertView.findViewById(R.id.horaInterTextView);
                viewHolder.hiperInterTextView = (TextView) convertView.findViewById(R.id.hiperInterTextView);
                viewHolder.hipoInterTextView  = (TextView) convertView.findViewById(R.id.hipoInterTextView);
                viewHolder.sedeInterTextView = (TextView) convertView.findViewById(R.id.sedeInterTextView);
                viewHolder.nauseaInterTextView = (TextView) convertView.findViewById(R.id.nauseaInterTextView);
                viewHolder.desmaioInterTextView = (TextView) convertView.findViewById(R.id.desmaioInterTextView);
                viewHolder.internacaoInterTextView  = (TextView) convertView.findViewById(R.id.internacaoInterTextView);
                viewHolder.anotacoesInterTextView = (TextView) convertView.findViewById(R.id.anotacoesInterTextView);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (IntercorrenciaActivity.IntercorrenciaArrayAdapter.ViewHolder)convertView.getTag();
            }

            Context context = getContext();
            viewHolder.dataInterTextView.setText(dgc.getDataIntercorrencia());
            viewHolder.horaInterTextView.setText(dgc.getHoraIntercorrencia());
            viewHolder.anotacoesInterTextView.setText(dgc.getAnotacoes());

            if (dgc.getHiperglicemia() == 1){
                viewHolder.hiperInterTextView.setText("Hiperglicemia");
            }else{
                viewHolder.hiperInterTextView.setText("");
                viewHolder.hiperInterTextView.setBackgroundResource(R.color.colorBlank);
            }

            if (dgc.getHipoglicemia() == 1){
                viewHolder.hipoInterTextView.setText("Hipoglicemia");
            }else{
                viewHolder.hipoInterTextView.setText("");
                viewHolder.hipoInterTextView.setBackgroundResource(R.color.colorBlank);
            }

            if (dgc.getSedeExcessiva() == 1){
                viewHolder.sedeInterTextView.setText("Sede Excessiva");
            }else{
                viewHolder.sedeInterTextView.setText("");
                viewHolder.sedeInterTextView.setBackgroundResource(R.color.colorBlank);
            }

            if (dgc.getNausea() == 1){
                viewHolder.nauseaInterTextView.setText("Naúsea e vômito");
            }else{
                viewHolder.nauseaInterTextView.setText("");
                viewHolder.nauseaInterTextView.setBackgroundResource(R.color.colorBlank);
            }

            if (dgc.getDesmaio() == 1){
                viewHolder.desmaioInterTextView.setText("Desmaio");
            }else{
                viewHolder.desmaioInterTextView.setText("");
                viewHolder.desmaioInterTextView.setBackgroundResource(R.color.colorBlank);
            }

            if (dgc.getInternacao() == 1){
                viewHolder.internacaoInterTextView.setText("Internação");
            }else{
                viewHolder.internacaoInterTextView.setText("");
                viewHolder.internacaoInterTextView.setBackgroundResource(R.color.colorBlank);
            }
            return convertView;
        }

    }


    private void configuraObserverShortClick(){
        intercorrenciaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dbuilder = new AlertDialog.Builder(IntercorrenciaActivity.this);
                dbuilder.setPositiveButton(getString(R.string.deletarItemHistorico), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intercorrencia intercorrencia = intercorrenciaList.get(position);
                        IntercorrenciaDAO intercorrenciaDAO = new IntercorrenciaDAO();
                        intercorrenciaDAO.excluir(intercorrencia.getId());
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
                Intent intent = new Intent(IntercorrenciaActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                IntercorrenciaActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }
}
