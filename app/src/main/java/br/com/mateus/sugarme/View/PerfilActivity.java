package br.com.mateus.sugarme.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.mateus.sugarme.Model.Perfil;
import br.com.mateus.sugarme.Controller.MedicalInfoPresenter;
import br.com.mateus.sugarme.Controller.PacientePresenter;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Singleton.UserSingleton;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.mateus.sugarme.Builder.DataBuilder.getTreamentProfile;
import static br.com.mateus.sugarme.Builder.DataBuilder.getTypeOfDiabetes;
import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtra;

public class PerfilActivity extends AppCompatActivity {


    private CircleImageView fotoPefilImageView;
    private TextView nomePerfilTextView;
    private TextView dataNasPerfilTextView;
    private TextView tipoPerfilTextView;
    private TextView tratamentoPerfilTextView;
    private TextView inicioPerfilTextView;
    private FloatingActionButton editOptFab;
    private AlertDialog alerta;

    private Perfil perfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Perfil);     //Titulo para ser exibido na sua Action Bar em frente à seta

        fotoPefilImageView = (CircleImageView) findViewById(R.id.fotoPefilImageView);
        nomePerfilTextView = (TextView) findViewById(R.id.nomePerfilTextView);
        dataNasPerfilTextView = (TextView) findViewById(R.id.dataNasPerfilTextView);
        tipoPerfilTextView = (TextView) findViewById(R.id.tipoPerfilTextView);
        tratamentoPerfilTextView = (TextView) findViewById(R.id.tratamentoPerfilTextView);
        inicioPerfilTextView = (TextView) findViewById(R.id.inicioPerfilTextView);
        editOptFab = (FloatingActionButton) findViewById(R.id.editOptFab);


        editOptFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditProfileOpt();
            }
        });

        //Parametros do PutExtra
        Intent it = getIntent();
        if(it != null && it.getExtras() != null) {
            perfil = (Perfil) it.getSerializableExtra("info");
            this.setPerfil(perfil);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
        this.nomePerfilTextView.setText(globalVariable.getNomeUser());
        this.dataNasPerfilTextView.setText(globalVariable.getDataNascUser());
        this.inicioPerfilTextView.setText(globalVariable.getInicioTratamento());
        this.tratamentoPerfilTextView.setText(globalVariable.getTratamento());
        this.tipoPerfilTextView.setText(globalVariable.getTipoDiabetes());
        if(globalVariable.getFotoPerfil() != null){
            this.fotoPefilImageView.setImageBitmap(globalVariable.getFotoPerfil());
        }else{
            this.fotoPefilImageView.setImageResource(R.drawable.perfil);
        }
        }

    public void setPerfil(Perfil perfil) {
        //Ajustar a escrita
        final UserSingleton globalVariable = (UserSingleton) getApplicationContext();

        this.nomePerfilTextView.setText(globalVariable.getNomeUser());
        this.dataNasPerfilTextView.setText(globalVariable.getDataNascUser());
        this.tipoPerfilTextView.setText(PerfilActivity.this.getString(R.string.perfil_tipo_diabetes, getTypeOfDiabetes(perfil.getTipoDiabetes())));
        this.inicioPerfilTextView.setText(PerfilActivity.this.getString(R.string.perfil_inicio, perfil.getAnoInicioTratamento()));
        this.tratamentoPerfilTextView.setText(getTreamentProfile(perfil.getMedicacao(), perfil.getInsulina(), perfil.getAlimentar(), perfil.getEsporte()  ));

        globalVariable.setInicioTratamento(this.inicioPerfilTextView.getText().toString());
        globalVariable.setTipoDiabetes(this.tipoPerfilTextView.getText().toString());
        globalVariable.setTratamento(this.tratamentoPerfilTextView.getText().toString());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FinishNavigation(PerfilActivity.this, PacienteActivity.class);
                break;
            default:break;
        }
        return true;
    }

    private void setEditProfileOpt() {
        //Lista de itens
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Perfil");
        itens.add("Foto");
        itens.add("Cadastro");

        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_menu_perfil_medico, itens);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("O que deseja editar?");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                switch (arg1){
                    case 0:{
                        MedicalInfoPresenter medicalInfoPresenter = new MedicalInfoPresenter();
                        medicalInfoPresenter.recebeInfoMedica(PerfilActivity.this);
                        break;
                    }
                    case 1: {
                        NavigationWithOnePutExtra(PerfilActivity.this, FotoPerfilActivity.class, "tipo", "paciente");
                        break;
                    }
                    case 2:{
                        PacientePresenter pacientePresenter = new PacientePresenter();
                        pacientePresenter.recebePaciente(PerfilActivity.this);
                        break;
                    }
                }
                alerta.dismiss();
            }
        });

        alerta = builder.create();
        alerta.show();
    }

}
