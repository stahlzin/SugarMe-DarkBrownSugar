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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private TextView UltimaLeituratextView;
    private TextView PenultimaLeituratextView;
    private TextView AnteLeituratextView;
    private TextView diarioHistorioTextView;
    private EditText glicemiaDiarioEditText;
    private EditText dataDiarioTextView;
    private EditText horaDiarioTextView;

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
