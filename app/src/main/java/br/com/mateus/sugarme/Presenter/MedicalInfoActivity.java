package br.com.mateus.sugarme.Presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.w3c.dom.Text;

import br.com.mateus.sugarme.Model.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.Model.MedicalInfoDAO;
import br.com.mateus.sugarme.Model.Model.Medico;
import br.com.mateus.sugarme.Model.Model.Paciente;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.SupervisingController.MedicalInfoController;

public class MedicalInfoActivity extends Activity {

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
}
