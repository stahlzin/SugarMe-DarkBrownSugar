package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import br.com.mateus.sugarme.Model.Intercorrencia;
import br.com.mateus.sugarme.Model.IntercorrenciaDAO;
import br.com.mateus.sugarme.Presenter.IntercorrenciaController;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Utils.MaskEditUtil;


public class IntercorrenciaInfo extends AppCompatActivity {
    private TextInputEditText textInputDtIntercorrencia;
    private TextInputEditText textInputAnotacao;

    private CheckBox checkBoxHipo;//hipoglicemia
    private CheckBox checkBoxNausea;//nausea
    private CheckBox checkBoxSede;//sede excessiva
    private CheckBox checkBoxHiper;//hiperglicemia
    private FloatingActionButton buttonSalvar;


    private IntercorrenciaController intercorrenciaController;
    private IntercorrenciaDAO intercorrenciaDAO;

    //OnCreate-------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercorrencia_info);

        //FindViewById dos objetos
        textInputDtIntercorrencia = (TextInputEditText) findViewById(R.id.textInputDtIntercorrencia);
        textInputAnotacao = (TextInputEditText) findViewById(R.id.textInputAnotacoes);
        checkBoxHipo = (CheckBox) findViewById(R.id.checkBoxHipoglicemia);
        checkBoxNausea = (CheckBox) findViewById(R.id.checkBoxNausea);
        checkBoxSede = (CheckBox) findViewById(R.id.checkBoxSede);
        checkBoxHiper = (CheckBox) findViewById(R.id.checkBoxHiperglicemia);
         buttonSalvar = (FloatingActionButton) findViewById(R.id.fabSalvar);

        //Máscara da data
        textInputDtIntercorrencia.addTextChangedListener(MaskEditUtil.mask(textInputDtIntercorrencia, MaskEditUtil.FORMAT_DATE_));


        //Salvar
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intercorrencia intercorrencia = preencheIntercorrencia();
                intercorrenciaController = new IntercorrenciaController();
                if(intercorrenciaController.isDadosOk(intercorrencia, IntercorrenciaInfo.this)) {
                    intercorrenciaDAO = new IntercorrenciaDAO();
                    intercorrenciaDAO.inserir(intercorrencia);
                    Toast.makeText(IntercorrenciaInfo.this, getString(R.string.inseridoSucesso), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IntercorrenciaInfo.this, PacienteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    IntercorrenciaInfo.this.startActivity(intent);
                }
            }
        });

    }

    //Preencher os dados da intercorrencia
    private Intercorrencia preencheIntercorrencia() {
        Intercorrencia intercorrencia = new Intercorrencia(
                textInputDtIntercorrencia.getText().toString(),textInputAnotacao.getText().toString(),
                checkBoxHiper.isChecked() ? 1 : 0,
                checkBoxHipo.isChecked() ? 1 : 0,
                checkBoxSede.isChecked() ? 1 : 0,
                checkBoxNausea.isChecked() ? 1 : 0);
        return  intercorrencia;
    }


    //onBackPressed
    @Override
    public void onBackPressed() {
            //Voltar a tela inicial
        Intent intent = new Intent(IntercorrenciaInfo.this, PacienteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        IntercorrenciaInfo.this.startActivity(intent);
    }

}