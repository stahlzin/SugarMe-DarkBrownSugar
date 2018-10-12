package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.DiarioGlicemicoDAO;
import br.com.mateus.sugarme.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.MedicalInfoDAO;
import br.com.mateus.sugarme.Presenter.MedicalInfoController;
import br.com.mateus.sugarme.R;

public class DiarioGlicemicoActivity extends AppCompatActivity {

    private TextView UltimaLeituratextView;
    private TextView PenultimaLeituratextView;
    private TextView AnteLeituratextView;
    private TextView diarioHistorioTextView;
    private EditText glicemiaDiarioEditText;
    private TextView dataDiarioTextView;
    private TextView horaDiarioTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario_glicemico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Diario);     //Titulo para ser exibido na sua Action Bar em frente à seta

        FloatingActionButton diarioFabAdd = (FloatingActionButton) findViewById(R.id.diarioFabAdd);
        diarioFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton diarioFabRemove = (FloatingActionButton) findViewById(R.id.diarioFabRemove);
        diarioFabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiarioGlicemico diarioGlicemico = registraLogDiario();
                DiarioGlicemicoDAO diarioGlicemicoDAO = new DiarioGlicemicoDAO();
                diarioGlicemicoDAO.inserir(diarioGlicemico);
                Toast.makeText(DiarioGlicemicoActivity.this, getString(R.string.inseridoSucesso), Toast.LENGTH_SHORT).show();

                }
            });

    }


    private DiarioGlicemico registraLogDiario (){
        DiarioGlicemico diarioGlicemico;

        diarioGlicemico = new DiarioGlicemico();
        diarioGlicemico.setGlicemia(Integer.parseInt(String.valueOf(glicemiaDiarioEditText.getText().toString())));
        diarioGlicemico.setData(dataDiarioTextView.getText().toString());
        diarioGlicemico.setHora(horaDiarioTextView.getText().toString());
        diarioGlicemico.setCategoria("Indefinido");
        diarioGlicemico.setGliTimestamp("Real Timestamp");

        return diarioGlicemico;
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
