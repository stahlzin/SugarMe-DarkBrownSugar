package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.mateus.sugarme.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.Model.PacienteDAO;
import br.com.mateus.sugarme.Utils.GlobalClass;

import br.com.mateus.sugarme.R;

import br.com.mateus.sugarme.Presenter.PacienteController;
import br.com.mateus.sugarme.Presenter.MedicalInfoController;

public class PacienteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //para deslogar
    PacienteController pacienteController = new PacienteController();
    private TextView nomePacienteTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nomePacienteTextView = (TextView) headerView.findViewById(R.id.nomePacienteTextView);
        navigationView.setNavigationItemSelectedListener(this);

        //Parametros do PutExtra
        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
                Paciente gPaciente = (Paciente) it.getSerializableExtra("paciente");
                this.setPacienteAsGlobal(gPaciente);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        this.nomePacienteTextView.setText(globalVariable.getNomeUser());
    }

    private void setPacienteAsGlobal(Paciente gPaciente) {
        this.nomePacienteTextView.setText(gPaciente.getNome());
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setDataNascUser(gPaciente.getDtNascimento());
        globalVariable.setNomeUser(gPaciente.getNome());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.paciente, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            MedicalInfoController medicalInfoController = new MedicalInfoController();
            medicalInfoController.getPerfil(PacienteActivity.this);
        } else if (id == R.id.nav_diario) {
            Intent intent = new Intent(PacienteActivity.this, DiarioGlicemicoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        } else if (id == R.id.nav_exames) {

        } else if (id == R.id.nav_intercor) {
            Intent intent = new Intent(PacienteActivity.this, IntercorrenciaInfo.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_medicacao) {

        } else if (id == R.id.nav_agenda) {

        } else if (id == R.id.nav_relatorio) {

        } else if (id == R.id.nav_chat) {

        } else if (id == R.id.nav_vincular) {

        } else if (id == R.id.nav_exames) {

        } else if (id == R.id.nav_sair) {
            pacienteController.logout();
            Intent intent = new Intent(PacienteActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PacienteActivity.this.startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}