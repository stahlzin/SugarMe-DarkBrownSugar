package br.com.mateus.sugarme.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.mateus.sugarme.Model.Intercorrencia;
import br.com.mateus.sugarme.DAO.IntercorrenciaDAO;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;

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
            TextView sintomasInterTextView;
            TextView anotacoesInterTextView;
            ImageView deleteIntercorrenciaImageView;
            String interId;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Intercorrencia dgc = getItem (position);
            final IntercorrenciaActivity.IntercorrenciaArrayAdapter.ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new IntercorrenciaActivity.IntercorrenciaArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_item_intercorrencia, parent, false);

                viewHolder.deleteIntercorrenciaImageView = (ImageView) convertView.findViewById(R.id.deleteIntercorrenciaImageView);
                viewHolder.dataInterTextView = (TextView) convertView.findViewById(R.id.dataInterTextView);
                viewHolder.horaInterTextView = (TextView) convertView.findViewById(R.id.horaInterTextView);
                viewHolder.sintomasInterTextView = (TextView) convertView.findViewById(R.id.sintomasInterTextView);
                viewHolder.anotacoesInterTextView = (TextView) convertView.findViewById(R.id.anotacoesInterTextView);

                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (IntercorrenciaActivity.IntercorrenciaArrayAdapter.ViewHolder)convertView.getTag();
            }

            final Context context = getContext();
            viewHolder.dataInterTextView.setText(dgc.getDataIntercorrencia());
            viewHolder.horaInterTextView.setText(dgc.getHoraIntercorrencia());
            viewHolder.anotacoesInterTextView.setText(dgc.getAnotacoes());
            viewHolder.interId = dgc.getId();

            //Passar Para o padrão Builder
            StringBuilder sintomas = new StringBuilder();
            if(dgc.getHipoglicemia() == 1){
                sintomas.append("Hipoglicemia\n");
            }
            if(dgc.getHiperglicemia() == 1){
                sintomas.append("Hiperglicemia\n");
            }
            if(dgc.getSedeExcessiva() == 1){
                sintomas.append("Sede Excessiva\n");
            }
            if(dgc.getNausea() == 1){
                sintomas.append("Náusea e vômito\n");
            }
            if(dgc.getDesmaio() == 1){
                sintomas.append("Desmaio\n");
            }
            if(dgc.getInternacao() == 1){
                sintomas.append("Internação\n");
            }
            if(dgc.getMiccao() == 1){
                sintomas.append("Micção Excessiva\n");
            }
            if(dgc.getVisão() == 1){
                sintomas.append("Visão embaçada\n");
            }
            if(dgc.getCansaso() == 1){
                sintomas.append("Cansaço excessivo\n");
            }
            if(dgc.getCaimbra() == 1){
                sintomas.append("Câimbra\n");
            }

            viewHolder.sintomasInterTextView.setText(sintomas);

            viewHolder.deleteIntercorrenciaImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dbuilder = new AlertDialog.Builder(context);
                    dbuilder.setTitle("Deseja excluir essa intercorrência?");
                    dbuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IntercorrenciaDAO intercorrenciaDAO = new IntercorrenciaDAO();
                            intercorrenciaDAO.excluir(viewHolder.interId);
                        }
                    }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dbuilder.show();


                }
            });

            return convertView;
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                FinishNavigation(IntercorrenciaActivity.this, PacienteActivity.class);
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Voltar a tela inicial
        FinishNavigation(IntercorrenciaActivity.this, PacienteActivity.class);
    }
}
