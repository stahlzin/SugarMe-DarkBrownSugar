package br.com.mateus.sugarme.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
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
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.Utils.GlobalClass;

import br.com.mateus.sugarme.R;

import br.com.mateus.sugarme.Presenter.PacientePresenter;
import br.com.mateus.sugarme.Presenter.MedicalInfoPresenter;

public class PacienteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //para deslogar
    PacientePresenter pacientePresenter = new PacientePresenter();
    private TextView nomePacienteTextView;
    private Button novaEntradaButton;
    private TextView valorUltimaTextView;
    private TextView statusUltimaTextView;
    private TextView dataUltimaTextView;
    private TextView horaUltimaTextView;
    private GridLayout ultimaLeituraGridLayout;
    private List<DiarioGlicemico> diarioGlicemicoList;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DiarioGlicemico diarioGlicemico;
    private DatabaseReference databaseReference;
    private LineChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name_PacienteAct);

        novaEntradaButton = (Button) findViewById(R.id.novaEntradaButton);
        valorUltimaTextView = (TextView) findViewById(R.id.valorUltimaTextView);
        statusUltimaTextView = (TextView) findViewById(R.id.statusUltimaTextView);
        dataUltimaTextView = (TextView) findViewById(R.id.dataUltimaTextView);
        horaUltimaTextView = (TextView) findViewById(R.id.horaUltimaTextView);
        ultimaLeituraGridLayout = (GridLayout) findViewById(R.id.ultimaLeituraGridLayout);
        chart = (LineChart) findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nomePacienteTextView = (TextView) headerView.findViewById(R.id.nomePacienteTextView);
        navigationView.setNavigationItemSelectedListener(this);

        novaEntradaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, DiarioGlicemicoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        ultimaLeituraGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, HistoricoDiarioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        setData();

        //Parametros do PutExtra
        Intent it = getIntent();
        if (it != null && it.getExtras() != null) {
            Paciente gPaciente = (Paciente) it.getSerializableExtra("paciente");
            this.setPacienteAsGlobal(gPaciente);
        }

    }

    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        this.nomePacienteTextView.setText(globalVariable.getNomeUser());

        diarioGlicemicoList = new ArrayList<DiarioGlicemico>();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("diario").orderByChild("gliTimestamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                diarioGlicemicoList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    DiarioGlicemico todos = json.getValue(DiarioGlicemico.class);
                    todos.setDiarioId(json.getKey());
                    diarioGlicemicoList.add(todos);
                }
                setValuesOfDiarioGlicemicoLast(diarioGlicemicoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setValuesOfDiarioGlicemicoLast(List<DiarioGlicemico> diarioGlicemicoList) {

        if(diarioGlicemicoList.size() != 0){
                valorUltimaTextView.setText(String.valueOf(diarioGlicemicoList.get(0).getGlicemia()));
                statusUltimaTextView.setText(diarioGlicemicoList.get(0).getCategoria());
                dataUltimaTextView.setText(diarioGlicemicoList.get(0).getData());
                horaUltimaTextView.setText(diarioGlicemicoList.get(0).getHora());
        }else{
            valorUltimaTextView.setText("");
            statusUltimaTextView.setText("");
            dataUltimaTextView.setText("");
            horaUltimaTextView.setText("");
        }
    }

    private void setPacienteAsGlobal(Paciente gPaciente) {
        if(gPaciente != null) {
            this.nomePacienteTextView.setText(gPaciente.getNome());
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            globalVariable.setDataNascUser(gPaciente.getDtNascimento());
            globalVariable.setNomeUser(gPaciente.getNome());
        }
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
            MedicalInfoPresenter medicalInfoPresenter = new MedicalInfoPresenter();
            medicalInfoPresenter.getPerfil(PacienteActivity.this);
        } else if (id == R.id.nav_diario) {
            Intent intent = new Intent(PacienteActivity.this, DiarioGlicemicoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        } else if (id == R.id.nav_exames) {
            Intent intent = new Intent(PacienteActivity.this, ExameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        } else if (id == R.id.nav_intercor) {
            Intent intent = new Intent(PacienteActivity.this, IntercorrenciaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_medicacao) {

        } else if (id == R.id.nav_agenda) {

        } else if (id == R.id.nav_relatorio) {

        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(PacienteActivity.this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_vincular) {
            Intent intent = new Intent(PacienteActivity.this, VinculoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_exames) {

        } else if (id == R.id.nav_sair) {
            pacientePresenter.logout();
            Intent intent = new Intent(PacienteActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PacienteActivity.this.startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setData() {
        //definição de valores (trazer do firebase)
        List<Entry> values = new ArrayList<Entry>();

            values.add(new Entry(1, 90 ));
            values.add(new Entry(2,150));

            values.add(new Entry(3, 95 ));
            values.add(new Entry(4,168));

            values.add(new Entry(5, 189 ));
            values.add(new Entry(6,35));

            values.add(new Entry(7, 30 ));
            values.add(new Entry(8,300));


            values.add(new Entry(9, 90 ));
            values.add(new Entry(10,150));

            values.add(new Entry(11, 95 ));
            values.add(new Entry(12,168));

            values.add(new Entry(13, 189 ));
            values.add(new Entry(14,35));

            values.add(new Entry(15, 30 ));

        LineDataSet dataSet = new LineDataSet(values, "15 últimas leituras");
        LineData lineData = new LineData(dataSet);

        //formatando eixos
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(15);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);

        YAxis y2Axis = chart.getAxisLeft();
        y2Axis.setDrawGridLines(false);

        //colocando os dados
        chart.setData(lineData);

        //setando o marker para ler dados ao clicar
        IMarker marker = new MyMarkerView(PacienteActivity.this, R.layout.custom_marker_view_layout);
        chart.setMarker(marker);

        }


    public class MyMarkerView extends MarkerView {

        private TextView tvContent;


        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            tvContent.setText("XX/XX/XXXX" + "\n" + "HH:MM" +"\n" +"Status " + e.getY());

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }
    }

}

