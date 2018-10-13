package br.com.mateus.sugarme.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.DiarioGlicemicoDAO;
import br.com.mateus.sugarme.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.MedicalInfoDAO;
import br.com.mateus.sugarme.Presenter.DiarioGlicemicoPresenter;
import br.com.mateus.sugarme.Presenter.MedicalInfoController;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Utils.MaskEditUtil;

import static br.com.mateus.sugarme.Utils.CoverterFactory.tryParseDatetoTimeStamp;
import static br.com.mateus.sugarme.Utils.CoverterFactory.tryParseInt;

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
                    Toast.makeText(DiarioGlicemicoActivity.this, getString(R.string.inseridoSucesso), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DiarioGlicemicoActivity.this, PacienteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    DiarioGlicemicoActivity.this.startActivity(intent);
                }

                }
            });

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
        diarioGlicemico.setGliTimestamp(tryParseDatetoTimeStamp(dataDiarioTextView.getText().toString(), horaDiarioTextView.getText().toString()).toString());
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

}
