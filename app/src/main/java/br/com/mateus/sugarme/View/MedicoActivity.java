package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import br.com.mateus.sugarme.Controller.MedicoPresenter;
import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.R;

public class MedicoActivity extends AppCompatActivity {
    private MedicoPresenter medicoPresenter;
    private Button novaEntradaButton;
    private Button buttonEditarMedico;
    private Medico medico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle(R.string.app_name);
        medicoPresenter = new MedicoPresenter();

        novaEntradaButton = (Button) findViewById(R.id.novaEntradaButton);

        //Logout Medico
        novaEntradaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicoPresenter.logout();
                Intent intent = new Intent(MedicoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MedicoActivity.this.startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.medico, menu);
        return true;
    }
}