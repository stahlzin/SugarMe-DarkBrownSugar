package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import br.com.mateus.sugarme.R;

import br.com.mateus.sugarme.Model.Perfil;
import br.com.mateus.sugarme.DAO.MedicalInfoDAO;
import br.com.mateus.sugarme.Controller.MedicalInfoPresenter;

public class MedicalInfoActivity extends AppCompatActivity {

    private TextInputEditText textInputPeso;
    private TextInputEditText textInputAltura;

    private TextInputEditText textAnoDescoberta;
    private TextInputEditText textAnoTratamento;

    private CheckBox checkBox1;//medicação
    private CheckBox checkBox2;//alimentar
    private CheckBox checkBox3;//insulina
    private CheckBox checkBox4;//esporte
    private CheckBox checkBox5;//colesterol
    private CheckBox checkBox6;//pressao alta
    private CheckBox checkBox7;//obesidade
    private CheckBox checkBox8;//triglicerideos
    private CheckBox checkBox9;//sedentarismo

    private RadioButton radioButtonTipo1;
    private RadioButton radioButtonTipo2;
    private RadioButton radioButtonTipo3;

    private TextView textViewEditarCadastro;

    private MedicalInfoPresenter medicalInfoPresenter;
    private MedicalInfoDAO medicalInfoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_info);

        //Botão superior de voltar e título
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_edPerfil);     //Titulo para ser exibido na sua Action Bar em frente à seta

        //FindViewById dos objetos
        textInputPeso = (TextInputEditText) findViewById(R.id.textInputPeso);
        textInputAltura = (TextInputEditText) findViewById(R.id.textInputAltura);
        radioButtonTipo1 = (RadioButton) findViewById(R.id.radioButtonTipo1);
        radioButtonTipo2 = (RadioButton) findViewById(R.id.radioButtonTipo2);
        radioButtonTipo3 = (RadioButton) findViewById(R.id.radioButtonTipo3);

        textAnoDescoberta = (TextInputEditText) findViewById(R.id.textAnoDescoberta);
        textAnoTratamento =(TextInputEditText) findViewById(R.id.textAnoTratamento);

        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
        checkBox7 = (CheckBox) findViewById(R.id.checkBox7);
        checkBox8 = (CheckBox) findViewById(R.id.checkBox8);
        checkBox9 = (CheckBox) findViewById(R.id.checkBox9);

        //Botão Salvar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Perfil perfil = preencheInfoMedica();
                medicalInfoPresenter = new MedicalInfoPresenter();
                if(medicalInfoPresenter.isDadosOk(perfil, MedicalInfoActivity.this)){
                    medicalInfoDAO = new MedicalInfoDAO();
                    medicalInfoDAO.inserir(perfil);
                    Toast.makeText(MedicalInfoActivity.this, getString(R.string.inseridoSucesso), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MedicalInfoActivity.this, PacienteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MedicalInfoActivity.this.startActivity(intent);
                }
            }
        });

        //Parametros do PutExtra
        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("edit").equals("editarInfo")) {
                Perfil perfil = (Perfil) it.getSerializableExtra("info");
                this.setMedicalInfo(perfil);
            }
        }
    } //Fim do OnCreate


    //OnBackPressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MedicalInfoActivity.this, PerfilActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MedicalInfoActivity.this.startActivity(intent);
    }

   private Perfil preencheInfoMedica(){
        Perfil perfil;
        if(radioButtonTipo1.isChecked()) {
            //int medicacao, int alimentar, int insulina, int esporte,
            // int colesterol, int pressaoAlta, int obesidade, int triglicerídeos, int sedentarismo
            perfil = new Perfil(textInputPeso.getText().toString(),
            textInputAltura.getText().toString(), "1", textAnoDescoberta.getText().toString(),
            textAnoTratamento.getText().toString(), checkBox1.isChecked() ? 1 : 0, checkBox4.isChecked() ? 1 : 0,
                    checkBox2.isChecked() ? 1 : 0, checkBox3.isChecked() ? 1 : 0,
                    checkBox5.isChecked() ? 1 : 0, checkBox6.isChecked() ? 1 : 0, checkBox7.isChecked() ? 1 : 0,
                    checkBox8.isChecked() ? 1 : 0, checkBox9.isChecked() ? 1 : 0);
            return perfil;
        }
        else if(radioButtonTipo2.isChecked()){
            perfil = new Perfil(textInputPeso.getText().toString(),
                    textInputAltura.getText().toString(), "2", textAnoDescoberta.getText().toString(),
                    textAnoTratamento.getText().toString(), checkBox1.isChecked() ? 1 : 0, checkBox4.isChecked() ? 1 : 0,
                    checkBox2.isChecked() ? 1 : 0, checkBox3.isChecked() ? 1 : 0,
                    checkBox5.isChecked() ? 1 : 0, checkBox6.isChecked() ? 1 : 0, checkBox7.isChecked() ? 1 : 0,
                    checkBox8.isChecked() ? 1 : 0, checkBox9.isChecked() ? 1 : 0);
            return perfil;
        }
        else if(radioButtonTipo3.isChecked()){
            perfil = new Perfil(textInputPeso.getText().toString(),
                    textInputAltura.getText().toString(), "3", textAnoDescoberta.getText().toString(),
                    textAnoTratamento.getText().toString(), checkBox1.isChecked() ? 1 : 0, checkBox4.isChecked() ? 1 : 0,
                    checkBox2.isChecked() ? 1 : 0, checkBox3.isChecked() ? 1 : 0,
                    checkBox5.isChecked() ? 1 : 0, checkBox6.isChecked() ? 1 : 0, checkBox7.isChecked() ? 1 : 0,
                    checkBox8.isChecked() ? 1 : 0, checkBox9.isChecked() ? 1 : 0);
            return perfil;
        }
        else{
            perfil = new Perfil(textInputPeso.getText().toString(),
                    textInputAltura.getText().toString(), "", textAnoDescoberta.getText().toString(),
                    textAnoTratamento.getText().toString(), checkBox1.isChecked() ? 1 : 0, checkBox4.isChecked() ? 1 : 0,
                    checkBox2.isChecked() ? 1 : 0, checkBox3.isChecked() ? 1 : 0,
                    checkBox5.isChecked() ? 1 : 0, checkBox6.isChecked() ? 1 : 0, checkBox7.isChecked() ? 1 : 0,
                    checkBox8.isChecked() ? 1 : 0, checkBox9.isChecked() ? 1 : 0);
            return perfil;
        }
    }


    //Preenchendo os TextInput, RadioButton e CheckBox
    public void setMedicalInfo(Perfil perfil) {
        this.textInputPeso.setText(perfil.getPeso());
        this.textInputAltura.setText(perfil.getAltura());
        if(perfil.getTipoDiabetes().equals("1")){
            this.radioButtonTipo1.setChecked(true);
        }
        else if(perfil.getTipoDiabetes().equals("2")){
            this.radioButtonTipo2.setChecked(true);
        }
        this.textAnoDescoberta.setText(perfil.getAnoDescoberta());
        this.textAnoTratamento.setText(perfil.getAnoInicioTratamento());
        if(perfil.getMedicacao()==1){
            this.checkBox1.setChecked(true);
        }
        if(perfil.getAlimentar()==1){
            this.checkBox4.setChecked(true);
        }
        if(perfil.getInsulina()==1){
            this.checkBox2.setChecked(true);
        }
        if(perfil.getEsporte()==1){
            this.checkBox3.setChecked(true);
        }
        if(perfil.getColesterol()==1){
            this.checkBox5.setChecked(true);
        }
        if(perfil.getPressaoAlta()==1){
            this.checkBox6.setChecked(true);
        }
        if(perfil.getObesidade()==1){
            this.checkBox7.setChecked(true);
        }
        if(perfil.getTriglicerídeos()==1){
            this.checkBox8.setChecked(true);
        }
        if(perfil.getSedentarismo()==1){
            this.checkBox9.setChecked(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(MedicalInfoActivity.this, PerfilActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MedicalInfoActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

}
