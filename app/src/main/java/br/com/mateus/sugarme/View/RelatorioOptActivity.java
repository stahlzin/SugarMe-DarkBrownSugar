package br.com.mateus.sugarme.View;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;

public class RelatorioOptActivity extends AppCompatActivity {
    //Atributos do XML
    private String tipoUsuario;
    private LineChart relChart;
    private TextView limHipoRelTextView;
    private TextView limHiperRelTextView;
    private TextView idRelTextView;
    private GridLayout abrirRelGridLayout;
    private GridLayout shareRelGridLayout;
    private Bitmap chart;
    private TableLayout relOptTL;

    //Intent get
    private String relUserID;
    private String relMes;
    private String relAno;


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
            }
        });

        setData();

        abrirRelGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String path= Environment.getExternalStorageDirectory()+"/SugarMe/Relatorios/";
                //relChart.saveToGallery("indice", path, "grafico", Bitmap.CompressFormat.PNG, 100);
                saveGraficToGallery();
            }
        });



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

    }

    private void readFromExternalStorage(String filename){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/SugarMe/Relatorios/"+ filename + ".pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");

        Intent intent = Intent.createChooser(target, "Abrir arquivo");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //Caso o usuário não tenha um visualizador de PDF, instrua-o aqui a baixar
        }
    }

    private void shareFileFromExternalStorage (String filename){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/SugarMe/Relatorios/"+ filename + ".pdf");
        final Uri arquivo = Uri.fromFile(file);
        final Intent _intent = new Intent();
        _intent.setAction(Intent.ACTION_SEND);
        _intent.putExtra(Intent.EXTRA_STREAM,  arquivo);
        _intent.putExtra(Intent.EXTRA_TEXT,  "Meu  PDF");
        _intent.putExtra(Intent.EXTRA_TITLE,   "Meu pdf");

        _intent.setType("application/pdf");

        startActivity(Intent.createChooser(_intent, "Compartilhar"));
    }


    public void saveGraficToGallery(){
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
        String outpath= Environment.getExternalStorageDirectory()+"/SugarMe/Relatorios/Relatorio1.pdf";


        try {
            PdfWriter.getInstance(document, new FileOutputStream(outpath));
            document.open();
            addMetaData(document);
            addTitlePage(document);
            addContent(document);
            document.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData(Document document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
    }

    private static void addTitlePage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Title of the document", catFont));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        addEmptyLine(preface, 3);
        preface.add(new Paragraph("This document describes something which is very important ",
                smallBold));

        addEmptyLine(preface, 8);

        preface.add(new Paragraph("This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
                redFont));

        document.add(preface);
        // Start a new page
        document.newPage();
    }

    private void addContent(Document document) throws DocumentException {
        Anchor anchor = new Anchor("First Chapter", catFont);
        anchor.setName("First Chapter");

        //add a grafic

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            chart.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(300,200);
            document.add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }




        // Second parameter is the number of the chapter
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

        Paragraph subPara = new Paragraph("Subcategory 1", subFont);
        Section subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("Hello"));

        subPara = new Paragraph("Subcategory 2", subFont);
        subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("Paragraph 1"));
        subCatPart.add(new Paragraph("Paragraph 2"));
        subCatPart.add(new Paragraph("Paragraph 3"));


        // add a list
        createList(subCatPart);
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 5);
        subCatPart.add(paragraph);

        // add a table
        createTable(subCatPart);

        // now add all this to the document
        document.add(catPart);

        // Next section
        anchor = new Anchor("Second Chapter", catFont);
        anchor.setName("Second Chapter");

        // Second parameter is the number of the chapter
        catPart = new Chapter(new Paragraph(anchor), 1);

        subPara = new Paragraph("Subcategory", subFont);
        subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("This is a very important message"));

        // now add all this to the document
        document.add(catPart);

    }

    private static void createTable(Section subCatPart)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 2"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 3"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        table.addCell("1.0");
        table.addCell("1.1");
        table.addCell("1.2");
        table.addCell("2.1");
        table.addCell("2.2");
        table.addCell("2.3");

        subCatPart.add(table);

    }

    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
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

        for (int i = 0; i < 10; i++) {
            float x = i + 2;
            float y = i * 5;
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
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(20);

        YAxis yAxis = relChart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);

        YAxis y2Axis = relChart.getAxisLeft();
        y2Axis.setDrawLabels(false);
        y2Axis.setDrawGridLines(false);

        //Limite entre hipo e hiperglicemia
        LimitLine upperLimitLine = new LimitLine(70);
        upperLimitLine.setLineColor(RelatorioOptActivity.this.getResources().getColor(R.color.colorRed));
        yAxis.addLimitLine(upperLimitLine);

        LimitLine lowerLimitLine = new LimitLine(100);
        lowerLimitLine.setLineColor(RelatorioOptActivity.this.getResources().getColor(R.color.colorRed));
        yAxis.addLimitLine(lowerLimitLine);

        //colocando os dados
        relChart.setData(lineData);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                if(tipoUsuario.equals("paciente")){
                    FinishNavigation(RelatorioOptActivity.this, PacienteActivity.class);
                }else if (tipoUsuario.equals("medico")){
                    FinishNavigation(RelatorioOptActivity.this, MedicoActivity.class);
                }
                break;
            default:break;
        }
        return true;
    }
}

