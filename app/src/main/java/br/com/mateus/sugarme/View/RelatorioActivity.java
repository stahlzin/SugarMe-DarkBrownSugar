package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Builder.DataBuilder.getDescExamList;
import static br.com.mateus.sugarme.Builder.DataBuilder.getMesList;
import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationToCreateReport;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtra;

public class RelatorioActivity extends AppCompatActivity {
    private String tipoUsuario;
    private String userId;
    private Button gerarRelButton;
    private Spinner mesRelSpinner;
    private EditText anoRelEditText;
    private ArrayList<String> mes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Relatorio);

        mesRelSpinner = (Spinner) findViewById(R.id.mesRelSpinner);
        gerarRelButton = (Button) findViewById(R.id.gerarRelButton);
        anoRelEditText = (EditText) findViewById(R.id.anoRelEditText);

        //Adapter pro spinner
        mes = getMesList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mes);
        mesRelSpinner.setAdapter(adapter);

        gerarRelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!anoRelEditText.getText().toString().equals("") ){
                    if(anoRelEditText.length() == 4){
                        NavigationToCreateReport(RelatorioActivity.this, RelatorioOptActivity.class, "tipo", "paciente", "mes", changeMouthToNumber(mesRelSpinner.getSelectedItem().toString()), "ano", anoRelEditText.getText().toString(), "id", userId );
                    }else{
                        Toast.makeText(RelatorioActivity.this, "Escolha um ano válido", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RelatorioActivity.this, "Escolha um ano", Toast.LENGTH_SHORT).show();
                }

            }
        });


        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("tipo").equals("paciente")) {
                tipoUsuario = "paciente";
            }
            else if(it.getStringExtra("tipo").equals("medico")){
                tipoUsuario = "medico";
            }
            userId = it.getStringExtra("userId");
        }

    }

    private String changeMouthToNumber (String mes){
        switch (mes){
            case "Janeiro":
                return "01";
            case "Fevereiro":
                return "02";
            case "Março":
                return "03";
            case "Abril":
                return "04";
            case "Maio":
                return "05";
            case "Junho":
                return "05";
            case "Julho":
                return "06";
            case "Agosto":
                return "08";
            case "Setembro":
                return "09";
            case "Outubro":
                return "10";
            case "Novembro":
                return "11";
            case "Dezembro":
                return "12";
            default:
                return "";
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                if(tipoUsuario.equals("paciente")){
                    FinishNavigation(RelatorioActivity.this, PacienteActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    FinishNavigation(RelatorioActivity.this, MedicoActivity.class);
                }
                break;
            default:break;
        }
        return true;
    }
}
