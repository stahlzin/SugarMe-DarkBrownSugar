package br.com.mateus.sugarme.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import br.com.mateus.sugarme.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.MedicalInfoDAO;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Presenter.MedicalInfoController;

public class MedicalInfoActivity extends AppCompatActivity {

    private TextInputEditText textInputMediaGlicemica;
    private TextInputEditText textInputPeso;
    private TextInputEditText textInputAltura;
    private Button buttonSalvar;
    private RadioButton radioButtonTipo1;
    private RadioButton radioButtonTipo2;
    private Button buttonVoltarMedicalInfo;

    private MedicalInfoController medicalInfoController;
    private MedicalInfoDAO medicalInfoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_info);

        //Botão superior de voltar e título
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Perfil");     //Titulo para ser exibido na sua Action Bar em frente à seta

        //FindViewById dos objetos
        textInputMediaGlicemica = (TextInputEditText) findViewById(R.id.textInputMediaGlicemica);
        textInputPeso = (TextInputEditText) findViewById(R.id.textInputPeso);
        textInputAltura = (TextInputEditText) findViewById(R.id.textInputAltura);
        buttonSalvar = (Button) findViewById(R.id.buttonSalvarInfoMedica);
        radioButtonTipo1 = (RadioButton) findViewById(R.id.radioButtonTipo1);
        radioButtonTipo2 = (RadioButton) findViewById(R.id.radioButtonTipo2);
        buttonVoltarMedicalInfo = (Button) findViewById(R.id.buttonVoltarMedicalInfo);


        //Botão Salvar
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedicalInfo medicalInfo = preencheInfoMedica();
                medicalInfoController = new MedicalInfoController();
                if(medicalInfoController.isDadosOk(medicalInfo, MedicalInfoActivity.this)){
                    medicalInfoDAO = new MedicalInfoDAO();
                    medicalInfoDAO.inserir(medicalInfo);
                    Toast.makeText(MedicalInfoActivity.this, getString(R.string.inseridoSucesso), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MedicalInfoActivity.this, PacienteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MedicalInfoActivity.this.startActivity(intent);
                }
            }
        });


        //Botão Voltar
        buttonVoltarMedicalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalInfoActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MedicalInfoActivity.this.startActivity(intent);
            }
        });

        //Parametros do PutExtra
        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("edit").equals("editarInfo")) {
                MedicalInfo medicalInfo = (MedicalInfo) it.getSerializableExtra("info");
                this.setMedicalInfo(medicalInfo);
            }
        }
    } //Fim do OnCreate


    //OnBackPressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MedicalInfoActivity.this, PacienteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MedicalInfoActivity.this.startActivity(intent);
    }
    private MedicalInfo preencheInfoMedica(){
        MedicalInfo medicalInfo;
        if(radioButtonTipo1.isChecked()) {
            medicalInfo = new MedicalInfo(textInputMediaGlicemica.getText().toString(),
                    textInputPeso.getText().toString(), textInputAltura.getText().toString(), "1");
            return medicalInfo;
        }
        else if(radioButtonTipo2.isChecked()){
            medicalInfo = new MedicalInfo(textInputMediaGlicemica.getText().toString(),
                    textInputPeso.getText().toString(), textInputAltura.getText().toString(), "2");
            return medicalInfo;
        }
        else{
            medicalInfo = new MedicalInfo(textInputMediaGlicemica.getText().toString(),
                    textInputPeso.getText().toString(), textInputAltura.getText().toString(), "");
            return medicalInfo;
        }
    }


    //Preenchendo os TextInput e RadioButton
    public void setMedicalInfo(MedicalInfo medicalInfo) {
        this.textInputMediaGlicemica.setText(medicalInfo.getMediaGlicemica());
        this.textInputPeso.setText(medicalInfo.getPeso());
        this.textInputAltura.setText(medicalInfo.getAltura());
        if(medicalInfo.getTipoDiabetes().equals("1")){
            this.radioButtonTipo1.setChecked(true);
        }
        else if(medicalInfo.getTipoDiabetes().equals("2")){
            this.radioButtonTipo2.setChecked(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(MedicalInfoActivity.this, PacienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MedicalInfoActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

}
