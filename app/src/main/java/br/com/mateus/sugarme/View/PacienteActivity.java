package br.com.mateus.sugarme.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;
import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.Singleton.UserSingleton;

import br.com.mateus.sugarme.R;

import br.com.mateus.sugarme.Controller.PacientePresenter;
import br.com.mateus.sugarme.Controller.PerfilController;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.toBitmap;
import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseInt;
import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtra;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtraAndUserId;
import static br.com.mateus.sugarme.Factory.NavigationFactory.SimpleNavigation;

public class PacienteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //para deslogar
    PacientePresenter pacientePresenter = new PacientePresenter();
    private TextView nomePacienteTextView;
    private CircleImageView fotoPacienteImageView;
    private Button novaEntradaButton;
    private TextView valorUltimaTextView;
    private TextView statusUltimaTextView;
    private TextView dataUltimaTextView;
    private TextView horaUltimaTextView;
    private TextView chatTextView;
    private GridLayout ultimaLeituraGridLayout;
    private GridLayout qteChatGridLayout;
    private List<DiarioGlicemico> diarioGlicemicoList;
    private List<DiarioGlicemico> graficDiarioGlicemicoList;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private LineChart chart;
    private TextView qteSemLerPChatTextView;
    private TextView limHipoTextView;
    private TextView limHiperTextView;
    private int hiperglicemiaPad;
    private int hipoglicemiaPad;
    private int chatFlag;



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
        chatTextView = (TextView) findViewById(R.id.chatTextView);
        ultimaLeituraGridLayout = findViewById(R.id.ultimaLeituraGridLayout);
        qteChatGridLayout = findViewById(R.id.qteChatGridLayout);
        limHipoTextView = findViewById(R.id.limHipoTextView);
        limHiperTextView = findViewById(R.id.limHiperTextView);

        chart = (LineChart) findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        qteSemLerPChatTextView = (TextView) findViewById(R.id.qteSemLerPChatTextView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nomePacienteTextView = (TextView) headerView.findViewById(R.id.nomePacienteTextView);
        fotoPacienteImageView = (CircleImageView) headerView.findViewById(R.id.fotoPacienteImageView);
        navigationView.setNavigationItemSelectedListener(this);

        novaEntradaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleNavigation(PacienteActivity.this, DiarioGlicemicoActivity.class);

            }
        });

        ultimaLeituraGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleNavigation(PacienteActivity.this, HistoricoDiarioActivity.class);

            }
        });
        qteChatGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleNavigation(PacienteActivity.this, VinculoChatActivity.class);
            }
        });

        getHipoHiperPad();
        //setData();


        //alterar a view e o menu se não aceitar chat
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("aceitaChat").getValue().equals("nao")){
                        qteChatGridLayout.setVisibility(View.INVISIBLE);
                        chatTextView.setVisibility(View.INVISIBLE);
                        qteChatGridLayout.setVisibility(View.GONE);
                        chatTextView.setVisibility(View.GONE);
                        chatFlag = 0;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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
        final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
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

       if(globalVariable.getFotoPerfil() == null){
           downloadImageProfile();
       }else{
           fotoPacienteImageView.setImageBitmap(globalVariable.getFotoPerfil());
       }


        //Ver conversas nao lidas
        databaseReference.child("users").child("pacientes").child(userId).child("notificacoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    qteSemLerPChatTextView.setText(Long.toString(dataSnapshot.getChildrenCount()));
                }
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
            final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
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
            NavigationWithOnePutExtra(PacienteActivity.this, ConfigurarActivity.class, "tipo", "paciente");
            return true;
        }if (id == R.id.action_about){
            NavigationWithOnePutExtra(PacienteActivity.this, SobreActivity.class, "tipo", "paciente");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            PerfilController perfilController = new PerfilController();
            perfilController.getPerfil(PacienteActivity.this);
        } else if (id == R.id.nav_diario) {
            SimpleNavigation (PacienteActivity.this, DiarioGlicemicoActivity.class);

        } else if (id == R.id.nav_exames) {
            SimpleNavigation(PacienteActivity.this, ExameActivity.class);

        } else if (id == R.id.nav_intercor) {
           SimpleNavigation(PacienteActivity.this, IntercorrenciaActivity.class);

        } else if (id == R.id.nav_relatorio) {
            getUserId();
            NavigationWithOnePutExtraAndUserId(PacienteActivity.this, RelatorioActivity.class, "tipo", "paciente", "userId", userId);
        } else if (id == R.id.nav_config) {
            NavigationWithOnePutExtra(PacienteActivity.this, ConfigurarActivity.class, "tipo", "paciente");

        } else if (id == R.id.nav_chat) {
            if(chatFlag == 0){
                AlertDialog.Builder dbuilder = new AlertDialog.Builder(PacienteActivity.this);
                dbuilder.setTitle("Chat desabilitado");
                dbuilder.setMessage("Deseja rever as opções de configuração?");
                dbuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavigationWithOnePutExtra(PacienteActivity.this, ConfigurarActivity.class, "tipo", "paciente");
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                dbuilder.show();
            }else{
                SimpleNavigation(PacienteActivity.this, VinculoChatActivity.class);
            }

        } else if (id == R.id.nav_vincular) {
            SimpleNavigation(PacienteActivity.this, VinculoActivity.class);

        } else if (id == R.id.nav_sair) {
            pacientePresenter.logout();
            FinishNavigation(PacienteActivity.this, MainActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * Métodos para colocar dados no gráfico
     */

    private void getHipoHiperPad(){
        //Hiper e Hipoglicemia Padrão
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.child("hipoglicemiaPadrao").getValue() == null){
                        hipoglicemiaPad = 70;
                    }else{
                        hipoglicemiaPad = tryParseInt(dataSnapshot.child("hipoglicemiaPadrao").getValue());
                    }

                    if(dataSnapshot.child("hiperglicemiaPadrao").getValue() == null){
                        hiperglicemiaPad = 200;
                    } else{
                        hiperglicemiaPad = tryParseInt(dataSnapshot.child("hiperglicemiaPadrao").getValue());
                    }


                    limHipoTextView.setText(getString(R.string.hipoPad, String.valueOf(hipoglicemiaPad)));
                    limHiperTextView.setText(getString(R.string.hiperPad, String.valueOf(hiperglicemiaPad)));
                    getDataFromFire ();
                                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getDataFromFire () {
        //Dados do firebase
        graficDiarioGlicemicoList = new ArrayList<DiarioGlicemico>();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("diario").orderByChild("gliTimestamp").limitToLast(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                graficDiarioGlicemicoList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    DiarioGlicemico todos = json.getValue(DiarioGlicemico.class);
                    todos.setDiarioId(json.getKey());
                    graficDiarioGlicemicoList.add(todos);
                }
                if(!graficDiarioGlicemicoList.isEmpty()){
                    setData();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void setData() {


        //definição de valores (trazer do firebase)
        List<Entry> values = new ArrayList<Entry>();

        for(int i = 0; i < graficDiarioGlicemicoList.size(); i++){
            float x = i + 1;
            float y = graficDiarioGlicemicoList.get(i).getGlicemia();
            values.add(new Entry(x, y));
        }

        LineDataSet dataSet = new LineDataSet(values, "Índice Glicêmico");
        LineData lineData = new LineData(dataSet);

        //formatando eixos
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(20);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);

        YAxis y2Axis = chart.getAxisLeft();
        y2Axis.setDrawLabels(false);
        y2Axis.setDrawGridLines(false);

        //Limite entre hipo e hiperglicemia
        LimitLine upperLimitLine = new LimitLine(hiperglicemiaPad);
        upperLimitLine.setLineColor(PacienteActivity.this.getResources().getColor(R.color.colorRed));
        yAxis.addLimitLine(upperLimitLine);

        LimitLine lowerLimitLine = new LimitLine(hipoglicemiaPad);
        lowerLimitLine.setLineColor(PacienteActivity.this.getResources().getColor(R.color.colorRed));
        yAxis.addLimitLine(lowerLimitLine);

        //colocando os dados
        chart.setData(lineData);
        chart.invalidate();

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

            int mIndex = ((int)e.getX() - 1);
            String mData = graficDiarioGlicemicoList.get(mIndex).getData();
            String mHora = graficDiarioGlicemicoList.get(mIndex).getHora();
            String mStatus = graficDiarioGlicemicoList.get(mIndex).getCategoria();


            tvContent.setText(mData + "\n" + mHora +"\n" + mStatus);

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

    /***
     * Método de download de foto para o perfil
     */

    public void downloadImageProfile() {
        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();


        //Download file in Memory
        StorageReference islandRef = storageRef.child("users").child("pacientes").child(userId).child("fotoPerfil/fotoPerfil.png");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
                globalVariable.setFotoPerfil(toBitmap(bytes));
                fotoPacienteImageView.setImageBitmap(globalVariable.getFotoPerfil());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}

