package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.mateus.sugarme.Controller.MedicoPresenter;
import br.com.mateus.sugarme.Model.Medico;
import br.com.mateus.sugarme.R;

public class MedicoActivity extends AppCompatActivity {
    private MedicoPresenter medicoPresenter;
    private Button buttonLogout;
    private Button buttonEditarMedico;
    private Medico medico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);
        medicoPresenter = new MedicoPresenter();

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonEditarMedico = (Button) findViewById(R.id.buttonEditarMedico);

        //Logout Medico
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicoPresenter.logout();
                Intent intent = new Intent(MedicoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MedicoActivity.this.startActivity(intent);
            }
        });


        //Editar Medico
        buttonEditarMedico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  medicoPresenter.recebeMedico(MedicoActivity.this);
            }
        });

    }
}
