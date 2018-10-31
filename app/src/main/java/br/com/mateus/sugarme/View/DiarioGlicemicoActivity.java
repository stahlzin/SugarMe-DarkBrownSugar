package br.com.mateus.sugarme.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.DAO.DiarioGlicemicoDAO;
import br.com.mateus.sugarme.Model.Intercorrencia;
import br.com.mateus.sugarme.DAO.IntercorrenciaDAO;
import br.com.mateus.sugarme.Controller.DiarioGlicemicoPresenter;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Builder.MaskEditUtil;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseDatetoTimeStamp;
import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseInt;

public class DiarioGlicemicoActivity extends AppCompatActivity {

    private TextView ultimaLeituratextView;
    private TextView penultimaLeituratextView;
    private TextView anteLeituratextView;
    private TextView diarioHistorioTextView;
    private EditText glicemiaDiarioEditText;
    private EditText dataDiarioTextView;
    private EditText horaDiarioTextView;
    private List<DiarioGlicemico> diarioGlicemicoList;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DiarioGlicemico diarioGlicemico;
    private DatabaseReference databaseReference;
    private AlertDialog alerta;
    private IntercorrenciaDAO intercorrenciaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario_glicemico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Diario);     //Titulo para ser exibido na sua Action Bar em frente à seta

        glicemiaDiarioEditText = (EditText) findViewById(R.id.glicemiaDiarioEditText);
        dataDiarioTextView = (EditText) findViewById(R.id.dataDiarioTextView);
        horaDiarioTextView = (EditText) findViewById(R.id.horaDiarioTextView);
        dataDiarioTextView.addTextChangedListener(MaskEditUtil.mask(dataDiarioTextView, MaskEditUtil.FORMAT_DATE));
        setValoresPadroes();
        diarioHistorioTextView = (TextView) findViewById(R.id.diarioHistorioTextView);

        ultimaLeituratextView = (TextView) findViewById(R.id.UltimaLeituratextView);
        penultimaLeituratextView = (TextView) findViewById(R.id.PenultimaLeituratextView);
        anteLeituratextView = (TextView) findViewById(R.id.AnteLeituratextView);


        diarioHistorioTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiarioGlicemicoActivity.this, HistoricoDiarioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


        ultimaLeituratextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int valor = tryParseInt(ultimaLeituratextView.getText().toString());
                int color = changeColor(valor);
                switch (color){
                    case -2:
                       ultimaLeituratextView.setBackgroundResource(R.color.colorHiper);
                        break;
                    case -1:
                        ultimaLeituratextView.setBackgroundResource(R.color.colorHipo);
                        break;
                    case 0:
                        ultimaLeituratextView.setBackgroundResource(R.color.colorNormal);
                        break;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        anteLeituratextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int valor = tryParseInt(anteLeituratextView.getText().toString());
                int color = changeColor(valor);
                switch (color){
                    case -2:
                        anteLeituratextView.setBackgroundResource(R.color.colorHiper);
                        break;
                    case -1:
                        anteLeituratextView.setBackgroundResource(R.color.colorHipo);
                        break;
                    case 0:
                        anteLeituratextView.setBackgroundResource(R.color.colorNormal);
                        break;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        penultimaLeituratextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int valor = tryParseInt(penultimaLeituratextView.getText().toString());
                int color = changeColor(valor);
                switch (color){
                    case -2:
                        penultimaLeituratextView.setBackgroundResource(R.color.colorHiper);
                        break;
                    case -1:
                        penultimaLeituratextView.setBackgroundResource(R.color.colorHipo);
                        break;
                    case 0:
                        penultimaLeituratextView.setBackgroundResource(R.color.colorNormal);
                        break;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        diarioGlicemicoList = new ArrayList<DiarioGlicemico>();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("diario").orderByChild("gliTimestamp").limitToLast(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                diarioGlicemicoList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    DiarioGlicemico todos = json.getValue(DiarioGlicemico.class);
                    todos.setDiarioId(json.getKey());
                    diarioGlicemicoList.add(todos);
                }
                setValuesOfDiarioGlicemicoList(diarioGlicemicoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FloatingActionButton diarioFabAdd = (FloatingActionButton) findViewById(R.id.diarioFabAdd);
        diarioFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int add = tryParseInt(glicemiaDiarioEditText.getText().toString());
                add++;
                glicemiaDiarioEditText.setText(String.valueOf(add));
            }
        });

        FloatingActionButton diarioFabRemove = (FloatingActionButton) findViewById(R.id.diarioFabRemove);
        diarioFabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int remove = tryParseInt(glicemiaDiarioEditText.getText().toString());
                remove--;
                glicemiaDiarioEditText.setText(String.valueOf(remove));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiarioGlicemico diarioGlicemico = registraLogDiario();
                DiarioGlicemicoPresenter diarioGlicemicoPresenter = new DiarioGlicemicoPresenter();
                if(diarioGlicemicoPresenter.isDadosOk(diarioGlicemico, DiarioGlicemicoActivity.this)){
                    DiarioGlicemicoDAO diarioGlicemicoDAO = new DiarioGlicemicoDAO();
                    diarioGlicemicoDAO.inserir(diarioGlicemico);

                    if(diarioGlicemico.getCategoria().equals("Hiperglicemia")){
                        confirmarIntercorrencia(diarioGlicemico);
                    }else if(diarioGlicemico.getCategoria().equals("Hipoglicemia")){
                        confirmarIntercorrencia(diarioGlicemico);
                    }else {
                        sucessoAdd();
                    }
                }
                }
            });
    }

    private void sucessoAdd() {
        Toast.makeText(DiarioGlicemicoActivity.this, getString(R.string.inseridoSucesso), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DiarioGlicemicoActivity.this, PacienteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        DiarioGlicemicoActivity.this.startActivity(intent);
    }

    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    private int changeColor(int valor) {
        if (valor <= 70) {
            return -1;
        }
        if (valor >= 200) {
            return -2;
        }
        return 0;
    }

    private void setValuesOfDiarioGlicemicoList(List<DiarioGlicemico> diarioGlicemicoList) {

        int ctrl = diarioGlicemicoList.size();

        switch (ctrl){
            case 1:
                anteLeituratextView.setText("--");
                penultimaLeituratextView.setText("--");
                ultimaLeituratextView.setText(String.valueOf(diarioGlicemicoList.get(0).getGlicemia()));
                break;
            case 2:
                anteLeituratextView.setText("--");
                penultimaLeituratextView.setText(String.valueOf(diarioGlicemicoList.get(0).getGlicemia()));
                ultimaLeituratextView.setText(String.valueOf(diarioGlicemicoList.get(1).getGlicemia()));
                break;
            case 3:
                anteLeituratextView.setText(String.valueOf(diarioGlicemicoList.get(0).getGlicemia()));
                penultimaLeituratextView.setText(String.valueOf(diarioGlicemicoList.get(1).getGlicemia()));
                ultimaLeituratextView.setText(String.valueOf(diarioGlicemicoList.get(2).getGlicemia()));
                break;
            default:
                anteLeituratextView.setText("--");
                penultimaLeituratextView.setText("--");
                ultimaLeituratextView.setText("--");
                break;
        }
    }


    private DiarioGlicemico registraLogDiario (){
        DiarioGlicemico diarioGlicemico;
        diarioGlicemico = new DiarioGlicemico();
        diarioGlicemico.setGlicemia(tryParseInt(glicemiaDiarioEditText.getText().toString()));
        diarioGlicemico.setData(dataDiarioTextView.getText().toString());
        diarioGlicemico.setHora(horaDiarioTextView.getText().toString());
        diarioGlicemico.setCategoria(createCategoria(diarioGlicemico));
        diarioGlicemico.setGliTimestamp(tryParseDatetoTimeStamp(dataDiarioTextView.getText().toString(), horaDiarioTextView.getText().toString()));
        return diarioGlicemico;
    }

    private void setValoresPadroes (){
        glicemiaDiarioEditText.setText("90");
        SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formataHora = new SimpleDateFormat("HH:mm");
        Date data = new Date();
        Date hora = new Date();
        String dataFormatada = formataData.format(data);
        String horaFormatada = formataHora.format(hora);
        dataDiarioTextView.setText(dataFormatada);
        horaDiarioTextView.setText(horaFormatada);

    }

    private String createCategoria(DiarioGlicemico diarioGlicemico){
        String anw = "Normal";
        int comp = tryParseInt(glicemiaDiarioEditText.getText().toString());

        if (comp <= 70){
            anw = "Hipoglicemia";
        }
        if (comp >= 200){
            anw = "Hiperglicemia";
        }

        return anw;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(DiarioGlicemicoActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                DiarioGlicemicoActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

    private void confirmarIntercorrencia(final DiarioGlicemico diarioGlicemico) {
        CharSequence[] charSequences = new CharSequence[]{"Cansaço", "Câimbra","Náusea e Vômito", "Sede Excessiva", "Visão Embaçada", "Micção Excessiva", "Desmaio", "Internação"};
        final boolean[] checados = new boolean[charSequences.length];
        final String status = diarioGlicemico.getCategoria();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Você tem algum sintoma associado à " + status + "?");
        builder.setMultiChoiceItems(charSequences, checados, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
                checados[arg1] = arg2;
            }
        });

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intercorrencia intercorrencia = new Intercorrencia();
                intercorrencia.setDataIntercorrencia(diarioGlicemico.getData());
                intercorrencia.setHoraIntercorrencia(diarioGlicemico.getHora());
                intercorrencia.setAnotacoes("Gerada automaticamente");
                if(status.equals("Hiperglicemia")){
                    intercorrencia.setHiperglicemia(1);
                    intercorrencia.setHipoglicemia(0);
                }else{
                    intercorrencia.setHiperglicemia(0);
                    intercorrencia.setHipoglicemia(1);
                }
                intercorrencia.setCansaso((checados[0]) ? 1 : 0);
                intercorrencia.setCaimbra((checados[1]) ? 1 : 0);
                intercorrencia.setNausea((checados[2]) ? 1 : 0);
                intercorrencia.setSedeExcessiva((checados[3]) ? 1 : 0);
                intercorrencia.setVisão((checados[4]) ? 1 : 0);
                intercorrencia.setMiccao((checados[5]) ? 1 : 0);
                intercorrencia.setDesmaio((checados[6]) ? 1 : 0);
                intercorrencia.setInternacao((checados[7]) ? 1 : 0);
                intercorrencia.setInterTimestamp(diarioGlicemico.getGliTimestamp());

                intercorrenciaDAO = new IntercorrenciaDAO();
                intercorrenciaDAO.inserir(intercorrencia);


                sucessoAdd();
            }
        });

        alerta = builder.create();
        alerta.show();
    }
}
