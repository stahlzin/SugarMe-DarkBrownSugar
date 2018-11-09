package br.com.mateus.sugarme.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.mateus.sugarme.Controller.MedicoPresenter;
import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtra;
import static br.com.mateus.sugarme.Factory.NavigationFactory.SimpleNavigation;

public class MedicoActivity extends AppCompatActivity {
    private MedicoPresenter medicoPresenter;
    private Button novaEntradaButton;
    private Button buttonEditarMedico;
    private Medico medico;
    private GridLayout perfilMedicoGridLayout;
    private GridLayout chatMedicoGridLayout;
    private GridLayout pacientesMedicoGridLayout;
    private GridLayout medicoConfiguracoesGridLayout;
    private AlertDialog alerta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle(R.string.app_name);
        medicoPresenter = new MedicoPresenter();

        perfilMedicoGridLayout = (GridLayout) findViewById(R.id.perfilMedicoGridLayout);
        chatMedicoGridLayout = (GridLayout) findViewById(R.id.chatMedicoGridLayout);
        pacientesMedicoGridLayout = (GridLayout) findViewById(R.id.pacientesMedicoGridLayout);
        medicoConfiguracoesGridLayout = (GridLayout) findViewById(R.id.medicoConfiguracoesGridLayout);

        //Configuração do Menu em GridLayout
       perfilMedicoGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditProfileType();


            }
        });

        chatMedicoGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chamar o chat
            }
        });

        pacientesMedicoGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Listar Pacientes, relatorio
            }
        });

        medicoConfiguracoesGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Configurações
            }
        });
        //Fim da configuração do Menu


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.medico, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            NavigationWithOnePutExtra(MedicoActivity.this, ConfigurarActivity.class, "tipo", "medico");
            return true;
        }
        else if (id == R.id.action_about){
            medicoPresenter.logout();
            FinishNavigation(MedicoActivity.this, MainActivity.class);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setEditProfileType() {
        //Lista de itens
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Perfil");
        itens.add("Foto");

        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_menu_perfil_medico, itens);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("O que deseja editar?");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                switch (arg1){
                    case 1: {
                        NavigationWithOnePutExtra(MedicoActivity.this, FotoPerfilActivity.class, "tipo", "medico");
                        break;
                    }
                    case 0:{
                        medicoPresenter.recebeMedico(MedicoActivity.this);
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