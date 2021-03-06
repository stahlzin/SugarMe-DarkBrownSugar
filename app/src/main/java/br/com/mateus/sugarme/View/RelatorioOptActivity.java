package br.com.mateus.sugarme.View;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import br.com.mateus.sugarme.BuildConfig;
import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.Intercorrencia;
import br.com.mateus.sugarme.Model.Paciente;
import br.com.mateus.sugarme.Model.Perfil;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseDatetoTimeStamp;
import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseInt;
import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.NavigationWithOnePutExtraAndUserId;
import static br.com.mateus.sugarme.Factory.NavigationFactory.SimpleNavigation;
import static br.com.mateus.sugarme.View.MainController.getUserId;
import static com.itextpdf.text.Chunk.IMAGE;
import static com.itextpdf.text.PageSize.A4;

public class RelatorioOptActivity extends AppCompatActivity {
    //Atributos do XML
    private String tipoUsuario;
    private LineChart relChart;
    private TextView limHipoRelTextView;
    private TextView limHiperRelTextView;
    private TextView idRelTextView;
    private TextView resumoRelTextView;
    private GridLayout abrirRelGridLayout;
    private GridLayout shareRelGridLayout;
    private Bitmap chart;
    private TableLayout relOptTL;

    //Intent get
    private String relUserID;
    private static String relMes;
    private static String relAno;

    //Parametros do relatorio
    private String relDataIn;
    private String relDataFim;
    private static String nomeUser;
    private String cUserId;
    private static java.util.List<DiarioGlicemico> diarioGlicemicoList = new ArrayList<>();
    private static java.util.List<Intercorrencia> intercorrenciaList = new ArrayList<>();
    private DatabaseReference databaseReference;
    static Paciente pacienteRel = new Paciente();
    static Perfil perfilRel = new Perfil();
    private int hiperglicemiaPad;
    private int hipoglicemiaPad;
    static int intCount = 0;
    static int hiperCount = 0;
    static int hipoCount = 0;

    //Atributos do iText
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Button gerarRelButton;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_opt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Relatorio);

        relChart = (LineChart) findViewById(R.id.relChart);
        relChart.getDescription().setEnabled(false);

        limHipoRelTextView = (TextView) findViewById(R.id.limHipoRelTextView);
        limHiperRelTextView = (TextView) findViewById(R.id.limHiperRelTextView);
        idRelTextView = (TextView) findViewById(R.id.idRelTextView);
        resumoRelTextView = (TextView) findViewById(R.id.resumoRelTextView);
        abrirRelGridLayout = (GridLayout) findViewById(R.id.abrirRelGridLayout);
        shareRelGridLayout = (GridLayout) findViewById(R.id.shareRelGridLayout);

        relOptTL = (TableLayout) findViewById(R.id.relOptTL);
        relOptTL.setVisibility(View.INVISIBLE);
        relOptTL.setVisibility(View.GONE);

        gerarRelButton = (Button) findViewById(R.id.gerarRelButton);
        gerarRelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relOptTL.setVisibility(View.VISIBLE);
                gerarRelButton.setVisibility(View.INVISIBLE);
                gerarRelButton.setVisibility(View.GONE);
                startReportWithChartTransformation();
            }
        });

       abrirRelGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readFromExternalStorage();
            }

        });

       shareRelGridLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               shareFileFromExternalStorage();
           }
       });


        //Dados do Intent
        Intent it = getIntent();
        if(it != null && it.getExtras() != null){
            if(it.getStringExtra("tipo").equals("paciente")) {
                tipoUsuario = "paciente";
            }
            else if(it.getStringExtra("tipo").equals("medico")){
                tipoUsuario = "medico";
            }
            relUserID = it.getStringExtra("id");
            relMes = it.getStringExtra("mes");
            relAno = it.getStringExtra("ano");
        }
        validateReport(relUserID, relMes, relAno);

    }

    /***
     * FACHADA para validar a existencia de dados no diário no período selecionada
     * Devolve para a act anterior senão tiver dados.
     * Se possuir, gerar a lista de intercorrências, recupera os dados do paciente e o perfil.
     * Gera e atualiza o grafico com todos os dados disponíveis
     */

    private void validateReport(final String userId, String mes, String ano){
        //data inicial e data final
        relDataIn = tryParseDatetoTimeStamp("01/"+mes+"/"+ano, "00:00");
        relDataFim = tryParseDatetoTimeStamp("31/"+mes+"/"+ano, "00:00");;

        //criar a list do diário glicêmico
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("diario").orderByChild("gliTimestamp").startAt(relDataIn).endAt(relDataFim).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                diarioGlicemicoList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    DiarioGlicemico todos = json.getValue(DiarioGlicemico.class);
                    todos.setDiarioId(json.getKey());
                    diarioGlicemicoList.add(todos);
                }
                //inverte a lista
                Collections.reverse(diarioGlicemicoList);
                if(diarioGlicemicoList.isEmpty()){
                    NavigationWithOnePutExtraAndUserId(RelatorioOptActivity.this, RelatorioActivity.class, "tipo", tipoUsuario, "userId", userId);
                    Toast.makeText(RelatorioOptActivity.this, "Não existem dados para o período selecionado", Toast.LENGTH_LONG).show();
                }else{
                    getHipoHiperPad(userId);
                    getAllReportData(userId, relDataIn, relDataFim );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getHipoHiperPad(String userId){
        //Hiper e Hipoglicemia Padrão
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
                    limHipoRelTextView.setText(getString(R.string.hipoPad, String.valueOf(hipoglicemiaPad)));
                    limHiperRelTextView.setText(getString(R.string.hiperPad, String.valueOf(hiperglicemiaPad)));
                    setData ();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getAllReportData(String userId, String relDataIn, String relDataFim) {
        //intercorrencia
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("Intercorrencias").orderByChild("interTimestamp").startAt(relDataIn).endAt(relDataFim).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                intercorrenciaList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    Intercorrencia todos = json.getValue(Intercorrencia.class);
                    todos.setId(json.getKey());
                    intercorrenciaList.add(todos);
                }
                //inverte a lista
                Collections.reverse(intercorrenciaList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Dados paciente
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("dados").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    pacienteRel.setNome(String.valueOf(dataSnapshot.child("nome").getValue()));
                    pacienteRel.setTelefone(String.valueOf(dataSnapshot.child("telefone").getValue()));
                    pacienteRel.setDtNascimento(String.valueOf(dataSnapshot.child("dtNascimento").getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Perfil paciente
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("InfoMedicas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    perfilRel.setAltura(String.valueOf(dataSnapshot.child("altura").getValue()));
                    perfilRel.setPeso(String.valueOf(dataSnapshot.child("peso").getValue()));
                    perfilRel.setTipoDiabetes(String.valueOf(dataSnapshot.child("tipoDiabetes").getValue()));
                    fillReportTemplateActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fillReportTemplateActivity (){
        StringBuilder idR = new StringBuilder();
        idR.append(pacienteRel.getNome());
        idR.append("\n");
        idR.append("Diabetes Tipo: ");
        idR.append(perfilRel.getTipoDiabetes());
        idR.append("\n");
        idR.append("Relatorio de: ");
        idR.append(relMes);
        idR.append("/");
        idR.append(relAno);

        idRelTextView.setText(idR.toString());

        StringBuilder resR = new StringBuilder();
        resR.append("Leituras Registradas: ");
        resR.append(String.valueOf(diarioGlicemicoList.size()));
        resR.append("\n");

        for (int i = 0; i < diarioGlicemicoList.size(); i ++){
            if (diarioGlicemicoList.get(i).getCategoria().equals("Hipoglicemia"))
                hipoCount ++;
        }
        resR.append("Episódios de Hipoglicemia: ");
        resR.append(String.valueOf(hipoCount));
        resR.append("\n");

        for (int i = 0; i < diarioGlicemicoList.size(); i ++){
            if (diarioGlicemicoList.get(i).getCategoria().equals("Hiperglicemia"))
                hiperCount ++;
        }
        resR.append("Episódios de Hiperglicemia: ");
        resR.append(String.valueOf(hiperCount));
        resR.append("\n\n");

        resR.append("Intercorrências Registradas: ");
        resR.append(String.valueOf(intercorrenciaList.size()));
        resR.append("\n");

        for (int i = 0; i < intercorrenciaList.size(); i ++){
            if (intercorrenciaList.get(i).getInternacao() == 1)
                intCount ++;
        }
        resR.append("Internações no período: ");
        resR.append(String.valueOf(intCount));

        resumoRelTextView.setText(resR.toString());

    }

    //-------------------------------------------------------------------------------//
    //***************************Fim da Fachada**************************************//
    //-------------------------------------------------------------------------------//


    /***
     * FACHADA do Relatorio em PDF
     * Chamado pelo botão gerar Relatorio, armazena um arquivo temporário que pode ser compartilhado ou aberto
     */

    private void readFromExternalStorage(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/SugarMe/Relatorios/Relatorio.pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        final Uri fileUri = FileProvider.getUriForFile(
                RelatorioOptActivity.this,
                RelatorioOptActivity.this.getApplicationContext().getPackageName() + ".provider",
                file);
        target.setDataAndType(fileUri, "application/pdf");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(target, "Abrir com"));
        } catch (ActivityNotFoundException e) {
            //Caso o usuário não tenha um visualizador de PDF, instrua-o aqui a baixar
        }
    }

    private void shareFileFromExternalStorage (){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/SugarMe/Relatorios/Relatorio.pdf");
        final Uri arquivo = FileProvider.getUriForFile(RelatorioOptActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        final Intent _intent = new Intent();
        _intent.setAction(Intent.ACTION_SEND);
        _intent.putExtra(Intent.EXTRA_STREAM,  arquivo);
        _intent.putExtra(Intent.EXTRA_TEXT,  "Meu  PDF");
        _intent.putExtra(Intent.EXTRA_TITLE,   "Meu pdf");

        _intent.setType("application/pdf");

        startActivity(Intent.createChooser(_intent, "Compartilhar"));
    }

    //Primeiro transformar o Gráfico em Bitmap
    public void startReportWithChartTransformation(){
        chart = relChart.getChartBitmap();
        verifyStoragePermissions(RelatorioOptActivity.this);
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);

            }


        } else {
            createPDF();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    createPDF();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(RelatorioOptActivity.this, "No podemos escribir sin tener permiso", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private static File getDirFromSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File sdcard = Environment.getExternalStorageDirectory()
                    .getAbsoluteFile();
            File dir = new File(sdcard, "SugarMe" + File.separator + "Relatorios");
            if (!dir.exists())
                dir.mkdirs();
            return dir;
        } else {
            return null;
        }
    }

    public void createPDF(){
        //create document object
        Document document=new Document();
        File dir = getDirFromSDCard();


        //output file path
        //String outpath= Environment.getExternalStorageDirectory()+"/theBestPdf/PDF1.pdf";
        String outpath= Environment.getExternalStorageDirectory()+"/SugarMe/Relatorios/Relatorio.pdf";


        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outpath));
            document.open();
            document.setPageSize(PageSize.A4);
/*
            //bkg
            PdfContentByte canvas = writer.getDirectContentUnder();
            Drawable d = getResources().getDrawable(R.drawable.relback);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(PageSize.A4);
            image.setAbsolutePosition(0, 0);
            canvas.addImage(image);
*/
            setBackground(writer);

            addMetaData(document);
            addTitlePage(document);

            setBackground(writer);
            addGrafPage(document);

            setBackground(writer);
            addDiarioItens(document);

            document.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData(Document document) {
        document.addTitle("Relatório de Acompanhamento");
        document.addSubject("SugarMe");
    }

    private void setBackground(PdfWriter writer){
        //bkg
        try {
        PdfContentByte canvas = writer.getDirectContentUnder();
        Drawable d = getResources().getDrawable(R.drawable.relback);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(PageSize.A4);
            image.setAbsolutePosition(0, 0);
            canvas.addImage(image);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }


    }


    private static void addTitlePage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Relatório de Acompanhamento", catFont));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        String s;
        Format formatter;
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm");
        Locale BRAZIL = new Locale("pt","BR");
        df = DateFormat.getDateInstance(DateFormat.FULL, BRAZIL);
        //  formatter = new SimpleDateFormat("MMMM");
        s = df.format(date);

        preface.add(new Paragraph(s, smallBold));
        addEmptyLine(preface, 3);
        preface.add(new Paragraph("Identificação ",
                smallBold));
        preface.add(new Paragraph("Paciente: " + pacienteRel.getNome()));
        preface.add(new Paragraph("Diabetes Tipo: " + perfilRel.getTipoDiabetes()));
        preface.add(new Paragraph("Relatorio de: " + relMes + "/"+ relAno));

        addEmptyLine(preface, 3);
        preface.add(new Paragraph("Resumo do Mês",
                smallBold));
        preface.add(new Paragraph("Leituras Registradas:  " + String.valueOf(diarioGlicemicoList.size())));
        preface.add(new Paragraph("Episódios de Hipoglicemia: " + String.valueOf(hipoCount)));
        preface.add(new Paragraph("Episódios de Hiperglicemia: " + String.valueOf(hiperCount)));
        preface.add(new Paragraph("Intercorrências Registradas: " + String.valueOf(intercorrenciaList.size())));
        preface.add(new Paragraph("Internações no período: " + (String.valueOf(intCount))));


        document.add(preface);
        // Start a new page
        document.newPage();
    }


    private void addGrafPage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Gráfico do Índice Glicêmico", catFont));
        addEmptyLine(preface, 1);

        document.add(preface);
        //add a grafic

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            chart.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(500,500);
            image.setAlignment(Image.MIDDLE);
            document.add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Paragraph pos = new Paragraph();
        pos.add(new Paragraph("Limite Hipglicemia:  " + String.valueOf(hipoglicemiaPad)));
        pos.add(new Paragraph("Limete Hiperglicemia:  " + String.valueOf(hiperglicemiaPad)));

        document.add(pos);
        document.newPage();


    }

    private void addDiarioItens(Document document)    throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Registros do Diário do Índice Glicêmico", catFont));
        addEmptyLine(preface, 1);

        Collections.reverse(diarioGlicemicoList);
        for(int i = 0; i < diarioGlicemicoList.size(); i++){
            preface.add(new Paragraph("Data: " + diarioGlicemicoList.get(i).getData() + " - Hora: " + diarioGlicemicoList.get(i).getHora()+ " - Valor registrado: " + String.valueOf(diarioGlicemicoList.get(i).getGlicemia()) + " - Categoria: " + diarioGlicemicoList.get(i).getCategoria()));
        }

        document.add(preface);
        document.newPage();
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


    /***
    * Metodos do gráfico
    */
    private void setData() {


        //definição de valores (trazer do firebase)
        java.util.List<Entry> values = new ArrayList<Entry>();

        for(int i = 0; i < diarioGlicemicoList.size(); i++){
            float x = i + 1;
            float y = diarioGlicemicoList.get(i).getGlicemia();
            values.add(new Entry(x, y));
        }

        LineDataSet dataSet = new LineDataSet(values, "Índice Glicêmico");
        LineData lineData = new LineData(dataSet);

        //formatando eixos
        XAxis xAxis = relChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = relChart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);

        YAxis y2Axis = relChart.getAxisLeft();
        y2Axis.setDrawLabels(true);
        y2Axis.setDrawGridLines(false);

        //Limite entre hipo e hiperglicemia
        LimitLine upperLimitLine = new LimitLine(hiperglicemiaPad);
        upperLimitLine.setLineColor(RelatorioOptActivity.this.getResources().getColor(R.color.colorRed));
        yAxis.addLimitLine(upperLimitLine);

        LimitLine lowerLimitLine = new LimitLine(hipoglicemiaPad);
        lowerLimitLine.setLineColor(RelatorioOptActivity.this.getResources().getColor(R.color.colorRed));
        yAxis.addLimitLine(lowerLimitLine);

        //colocando os dados
        relChart.setData(lineData);
        relChart.invalidate();
    }


    /***
     * Metodos de Navegação
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                if(tipoUsuario.equals("paciente")){
                    FinishNavigation(RelatorioOptActivity.this, PacienteActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    FinishNavigation(RelatorioOptActivity.this, PacientesVinculadosActivity.class);
                }
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(tipoUsuario.equals("paciente")){
            FinishNavigation(RelatorioOptActivity.this, PacienteActivity.class);
        }else if (tipoUsuario.equals("medico")){
            FinishNavigation(RelatorioOptActivity.this, PacientesVinculadosActivity.class);
        }
    }
}

