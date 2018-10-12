package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.mateus.sugarme.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.MedicalInfoDAO;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.Model.PacienteDAO;
import br.com.mateus.sugarme.Presenter.MedicalInfoController;
import br.com.mateus.sugarme.Presenter.PacienteController;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Utils.GlobalClass;

public class PerfilActivity extends AppCompatActivity {


    private ImageView fotoPefilImageView;
    private TextView nomePerfilTextView;
    private TextView dataNasPerfilTextView;
    private TextView tipoPerfilTextView;
    private TextView tratamentoPerfilTextView;
    private TextView inicioPerfilTextView;

    private MedicalInfoController medicalInfoController;
    private MedicalInfoDAO medicalInfoDAO;
    private Paciente paciente;


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
            MedicalInfo medicalInfo = (MedicalInfo) it.getSerializableExtra("info");
            this.setPerfil(medicalInfo);
        }

    }

    public void setPerfil(MedicalInfo medicalInfo) {
        //Ajustar a escrita

        this.tipoPerfilTextView.setText("Tipo Diabetes: " + medicalInfo.getTipoDiabetes());
        //this.tratamentoPerfilTextView.setText(medicalInfo.getInsulina());
        this.inicioPerfilTextView.setText("Ano do Início do Tratamento: " + medicalInfo.getAnoInicioTratamento());

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
            MedicalInfoController medicalInfoController = new MedicalInfoController();
            medicalInfoController.recebeInfoMedica(PerfilActivity.this);
            return true;
        }
        if (id == R.id.action_gerenciar_cadastro) {
            PacienteController pacienteController = new PacienteController();
            pacienteController.recebePaciente(PerfilActivity.this);
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
