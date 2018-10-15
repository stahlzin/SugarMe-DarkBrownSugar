package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.mateus.sugarme.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.MedicalInfoDAO;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.Presenter.MedicalInfoPresenter;
import br.com.mateus.sugarme.Presenter.PacientePresenter;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Utils.GlobalClass;

public class PerfilActivity extends AppCompatActivity {


    private ImageView fotoPefilImageView;
    private TextView nomePerfilTextView;
    private TextView dataNasPerfilTextView;
    private TextView tipoPerfilTextView;
    private TextView tratamentoPerfilTextView;
    private TextView inicioPerfilTextView;

    private MedicalInfoPresenter medicalInfoPresenter;
    private MedicalInfoDAO medicalInfoDAO;
    private Paciente paciente;
    private MedicalInfo medicalInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Perfil);     //Titulo para ser exibido na sua Action Bar em frente à seta

        fotoPefilImageView = (ImageView) findViewById(R.id.fotoPefilImageView);
        nomePerfilTextView = (TextView) findViewById(R.id.nomePerfilTextView);
        dataNasPerfilTextView = (TextView) findViewById(R.id.dataNasPerfilTextView);
        tipoPerfilTextView = (TextView) findViewById(R.id.tipoPerfilTextView);
        tratamentoPerfilTextView = (TextView) findViewById(R.id.tratamentoPerfilTextView);
        inicioPerfilTextView = (TextView) findViewById(R.id.inicioPerfilTextView);



        //Parametros do PutExtra
        Intent it = getIntent();
        if(it != null && it.getExtras() != null) {
            medicalInfo = (MedicalInfo) it.getSerializableExtra("info");
            this.setPerfil(medicalInfo);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        this.nomePerfilTextView.setText(globalVariable.getNomeUser());
        this.dataNasPerfilTextView.setText(globalVariable.getDataNascUser());
        this.inicioPerfilTextView.setText(globalVariable.getInicioTratamento());
        this.tratamentoPerfilTextView.setText(globalVariable.getTratamento());
        this.tipoPerfilTextView.setText(globalVariable.getTipoDiabetes());
    }

    public void setPerfil(MedicalInfo medicalInfo) {
        //Ajustar a escrita
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        this.nomePerfilTextView.setText(globalVariable.getNomeUser());
        this.dataNasPerfilTextView.setText(globalVariable.getDataNascUser());


        String tipoDiabetes = medicalInfo.getTipoDiabetes();
        String infoTipo;
        switch (tipoDiabetes){
            case ("1"): infoTipo = "1"; break;
            case ("2"): infoTipo = "2";break;
            case ("3"): infoTipo = "Gestacional"; break;
            default: infoTipo = ""; break;
        }
        this.tipoPerfilTextView.setText("Diabetes Tipo: " + infoTipo);
        this.inicioPerfilTextView.setText("Tratamento iniciou em: " + medicalInfo.getAnoInicioTratamento());

        StringBuilder perfilTratamento = new StringBuilder("");
        if(medicalInfo.getMedicacao() == 1){
            perfilTratamento.append("Medicação\n");
        }
        if(medicalInfo.getInsulina() == 1){
            perfilTratamento.append("Insulina\n");
        }
        if(medicalInfo.getAlimentar() == 1){
            perfilTratamento.append("Dieta Restritiva\n");
        }
        if(medicalInfo.getEsporte() == 1){
            perfilTratamento.append("Prática de Atividades Físicas\n");
        }
        this.tratamentoPerfilTextView.setText(perfilTratamento);

        globalVariable.setInicioTratamento(this.inicioPerfilTextView.getText().toString());
        globalVariable.setTipoDiabetes(this.tipoPerfilTextView.getText().toString());
        globalVariable.setTratamento(this.tratamentoPerfilTextView.getText().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editar_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_editar_perfil) {
            MedicalInfoPresenter medicalInfoPresenter = new MedicalInfoPresenter();
            medicalInfoPresenter.recebeInfoMedica(PerfilActivity.this);
            return true;
        }
        if (id == R.id.action_gerenciar_cadastro) {
            PacientePresenter pacientePresenter = new PacientePresenter();
            pacientePresenter.recebePaciente(PerfilActivity.this);
            return true;
        }
        if (id== android.R.id.home) {
            Intent intent = new Intent(PerfilActivity.this, PacienteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PerfilActivity.this.startActivity(intent);
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

}
