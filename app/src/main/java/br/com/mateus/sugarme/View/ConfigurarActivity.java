package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;

import br.com.mateus.sugarme.Controller.MedicoPresenter;
import br.com.mateus.sugarme.Controller.PacientePresenter;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;

public class ConfigurarActivity extends AppCompatActivity {

    private PacientePresenter pacientePresenter;
    private MedicoPresenter medicoPresenter;
    private Switch aceitaChatswitch;
    private Switch compartilhaDiarioSwitch;
    private EditText padHipoEditText;
    private EditText padHiperEditText;
    private GridLayout standardValuesGridLayout;
    private TextView standardValuesTextView;
    private GridLayout compartilhaDiarioGridLayout;
    private Button logoutButton;
    private Button salvarAlterButton;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Configuracoes);

        pacientePresenter = new PacientePresenter();
        medicoPresenter = new MedicoPresenter();

        aceitaChatswitch = (Switch) findViewById(R.id.aceitaChatswitch);
        compartilhaDiarioSwitch = (Switch) findViewById(R.id.compartilhaDiarioSwitch);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        salvarAlterButton = (Button) findViewById(R.id.salvarAlterButton);
        standardValuesGridLayout = (GridLayout) findViewById(R.id.standardValuesGridLayout);
        standardValuesTextView = (TextView) findViewById(R.id.standardValuesTextView);
        compartilhaDiarioGridLayout = (GridLayout) findViewById(R.id.compartilhaDiarioGridLayout);

        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("tipo").equals("paciente")) {
                tipoUsuario = "paciente";
            }
            else if(it.getStringExtra("tipo").equals("medico")){
                tipoUsuario = "medico";
                standardValuesGridLayout.setVisibility(View.INVISIBLE);
                standardValuesGridLayout.setVisibility(View.GONE);
                standardValuesTextView.setVisibility(View.INVISIBLE);
                standardValuesTextView.setVisibility(View.GONE);
                compartilhaDiarioGridLayout.setVisibility(View.INVISIBLE);
                compartilhaDiarioGridLayout.setVisibility(View.GONE);
            }
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipoUsuario.equals("paciente")){
                    pacientePresenter.logout();
                    FinishNavigation(ConfigurarActivity.this, MainActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    medicoPresenter.logout();
                    FinishNavigation(ConfigurarActivity.this, MainActivity.class);
                }
            }
        });

        salvarAlterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //tratar o preenchimento auto
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                if(tipoUsuario.equals("paciente")){
                    FinishNavigation(ConfigurarActivity.this, PacienteActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    FinishNavigation(ConfigurarActivity.this, PacienteActivity.class);
                }
                break;
            default:break;
        }
        return true;
    }
}
