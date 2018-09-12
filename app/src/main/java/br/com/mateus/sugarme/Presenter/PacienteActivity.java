package br.com.mateus.sugarme.Presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.mateus.sugarme.SupervisingController.MedicalInfoController;
import br.com.mateus.sugarme.SupervisingController.PacienteController;
import br.com.mateus.sugarme.R;

public class PacienteActivity extends AppCompatActivity {
    private PacienteController pacienteController;
    private Button buttonLogoutPaciente;
    private Button buttonEditarPaciente;
    private Button buttonInformacoesMedicas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
        pacienteController = new PacienteController();

        buttonLogoutPaciente = (Button) findViewById(R.id.buttonLogoutPaciente);
        buttonEditarPaciente = (Button) findViewById(R.id.buttonEditarPaciente);
        buttonInformacoesMedicas = (Button) findViewById(R.id.buttonInformacoesMedicas);

        //Logout Paciente
        buttonLogoutPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pacienteController.logout();
                Intent intent = new Intent(PacienteActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PacienteActivity.this.startActivity(intent);

            }
        });

        //Editar Paciente
        buttonEditarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pacienteController.recebePaciente(PacienteActivity.this);
            }
        });

        buttonInformacoesMedicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Informações medicas
                MedicalInfoController medicalInfoController = new MedicalInfoController();
                medicalInfoController.recebeInfoMedica(PacienteActivity.this);
//                Intent intent = new Intent(PacienteActivity.this, MedicalInfoActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                PacienteActivity.this.startActivity(intent);
            }
        });

    }
}
