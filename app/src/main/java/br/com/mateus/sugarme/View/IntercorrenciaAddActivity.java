package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.mateus.sugarme.Model.Intercorrencia;
import br.com.mateus.sugarme.DAO.IntercorrenciaDAO;
import br.com.mateus.sugarme.Controller.IntercorrenciaPresenter;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Builder.CoverterFactory.tryParseDatetoTimeStamp;


public class IntercorrenciaAddActivity extends AppCompatActivity {
    private TextInputEditText textInputDtIntercorrencia;
    private TextInputEditText textInputAnotacao;
    private TextInputEditText textInputHrIntercorrencia;

    private CheckBox checkBoxHipo;//hipoglicemia
    private CheckBox checkBoxNausea;//nausea
    private CheckBox checkBoxSede;//sede excessiva
    private CheckBox checkBoxHiper;//hiperglicemia
    private CheckBox checkBoxDesmaio;//desmaio
    private CheckBox checkBoxInternacao; //internação
    private CheckBox checkBoxCansaco;
    private CheckBox checkBoxCaimbra;
    private CheckBox checkBoxVisao;
    private CheckBox checkBoxMiccao;
    private FloatingActionButton buttonSalvar;


    private IntercorrenciaPresenter intercorrenciaPresenter;
    private IntercorrenciaDAO intercorrenciaDAO;

    //OnCreate-------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercorrencia_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Add_Inter);     //Titulo para ser exibido na sua Action Bar em frente à seta

        //FindViewById dos objetos
        textInputDtIntercorrencia = (TextInputEditText) findViewById(R.id.textInputDtIntercorrencia);
        textInputHrIntercorrencia = (TextInputEditText) findViewById(R.id.textInputHrIntercorrencia);
        textInputAnotacao = (TextInputEditText) findViewById(R.id.textInputAnotacoes);
        checkBoxHipo = (CheckBox) findViewById(R.id.checkBoxHipoglicemia);
        checkBoxNausea = (CheckBox) findViewById(R.id.checkBoxNausea);
        checkBoxSede = (CheckBox) findViewById(R.id.checkBoxSede);
        checkBoxHiper = (CheckBox) findViewById(R.id.checkBoxHiperglicemia);
        checkBoxDesmaio = (CheckBox) findViewById(R.id.checkBoxDesmaio);
        checkBoxInternacao = (CheckBox) findViewById(R.id.checkBoxInternacao);
        checkBoxVisao = (CheckBox) findViewById(R.id.checkBoxVisao);
        checkBoxMiccao = (CheckBox) findViewById(R.id.checkBoxMiccao);
        checkBoxCaimbra = (CheckBox) findViewById(R.id.checkBoxCaimbra);
        checkBoxCansaco = (CheckBox) findViewById(R.id.checkBoxCansaco);
        buttonSalvar = (FloatingActionButton) findViewById(R.id.fabSalvar);

        //Máscara da data
//       textInputDtIntercorrencia.addTextChangedListener(MaskEditUtil.mask(textInputDtIntercorrencia, MaskEditUtil.FORMAT_DATE_));

        //Salvar
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intercorrencia intercorrencia = preencheIntercorrencia();
                intercorrenciaPresenter = new IntercorrenciaPresenter();
                if(intercorrenciaPresenter.isDadosOk(intercorrencia, IntercorrenciaAddActivity.this)) {
                    intercorrenciaDAO = new IntercorrenciaDAO();
                    intercorrenciaDAO.inserir(intercorrencia);
                    Toast.makeText(IntercorrenciaAddActivity.this, getString(R.string.inseridoSucesso), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IntercorrenciaAddActivity.this, IntercorrenciaActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    IntercorrenciaAddActivity.this.startActivity(intent);
                }
            }
        });

        setValoresPadroes ();
    }

    //Preencher os dados da intercorrencia
    private Intercorrencia preencheIntercorrencia() {
        Intercorrencia intercorrencia = new Intercorrencia();
        intercorrencia.setDataIntercorrencia(textInputDtIntercorrencia.getText().toString());
        intercorrencia.setHoraIntercorrencia(textInputHrIntercorrencia.getText().toString());
        intercorrencia.setAnotacoes(textInputAnotacao.getText().toString());
        intercorrencia.setHiperglicemia(checkBoxHiper.isChecked() ? 1 : 0);
        intercorrencia.setHipoglicemia(checkBoxHipo.isChecked() ? 1 : 0);
        intercorrencia.setDesmaio(checkBoxDesmaio.isChecked() ? 1 : 0);
        intercorrencia.setInternacao(checkBoxInternacao.isChecked() ? 1 : 0);
        intercorrencia.setNausea(checkBoxNausea.isChecked() ? 1 : 0);
        intercorrencia.setSedeExcessiva(checkBoxSede.isChecked() ? 1 : 0);
        intercorrencia.setCaimbra(checkBoxCaimbra.isChecked()? 1:0);
        intercorrencia.setCansaso(checkBoxCansaco.isChecked()?1:0);
        intercorrencia.setVisão(checkBoxVisao.isChecked() ? 1:0);
        intercorrencia.setMiccao(checkBoxMiccao.isChecked() ? 1:0);
        intercorrencia.setInterTimestamp(tryParseDatetoTimeStamp(textInputDtIntercorrencia.getText().toString(), textInputHrIntercorrencia.getText().toString()));
        return  intercorrencia;
    }


    private void setValoresPadroes (){
        SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formataHora = new SimpleDateFormat("HH:mm");
        Date data = new Date();
        Date hora = new Date();
        String dataFormatada = formataData.format(data);
        String horaFormatada = formataHora.format(hora);
        textInputDtIntercorrencia.setText(dataFormatada);
        textInputHrIntercorrencia.setText(horaFormatada);
    }


    //onBackPressed
    @Override
    public void onBackPressed() {
            //Voltar a tela inicial
        Intent intent = new Intent(IntercorrenciaAddActivity.this, PacienteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        IntercorrenciaAddActivity.this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(IntercorrenciaAddActivity.this, IntercorrenciaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                IntercorrenciaAddActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

}