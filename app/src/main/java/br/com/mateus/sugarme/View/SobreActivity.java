package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.SimpleNavigation;

public class SobreActivity extends AppCompatActivity {

    private Button OKbutton;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Sobre);

        OKbutton = (Button) findViewById(R.id.OKbutton);

        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("tipo").equals("paciente")) {
                tipoUsuario = "paciente";
            }
            else if(it.getStringExtra("tipo").equals("medico")){
                tipoUsuario = "medico";
            }
        }

        OKbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipoUsuario.equals("paciente")){
                    SimpleNavigation (SobreActivity.this, PacienteActivity.class);
                }
                if(tipoUsuario.equals("medico")){
                    SimpleNavigation (SobreActivity.this, MedicoActivity.class);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                if(tipoUsuario.equals("paciente")){
                    FinishNavigation(SobreActivity.this, PacienteActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    FinishNavigation(SobreActivity.this, MedicoActivity.class);
                }
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(tipoUsuario.equals("paciente")){
            FinishNavigation(SobreActivity.this, PacienteActivity.class);
        }else if (tipoUsuario.equals("medico")){
            FinishNavigation(SobreActivity.this, MedicoActivity.class);
        }
    }
}
