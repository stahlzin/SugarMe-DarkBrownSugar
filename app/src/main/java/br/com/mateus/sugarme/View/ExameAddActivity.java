package br.com.mateus.sugarme.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.mateus.sugarme.Model.Exame;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Utils.MaskEditUtil;

import static br.com.mateus.sugarme.Utils.CoverterFactory.tryParseDatetoTimeStamp;

public class ExameAddActivity extends AppCompatActivity {

    Button uploadExameButton;
    TextView statusExameTextView;
    FloatingActionButton addfloatingActionButton;
    TextInputEditText dataExameTextInput, descricaoExameTextInput;
    Uri uriPDF;
    ProgressDialog progressDialog;
    String userId;

    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exame_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_AddExame);     //Titulo para ser exibido na sua Action Bar em frente à seta

        uploadExameButton = (Button) findViewById(R.id.uploadExameButton);
        statusExameTextView = (TextView) findViewById(R.id.statusExameTextView);
        addfloatingActionButton = (FloatingActionButton) findViewById(R.id.addfloatingActionButton);
        dataExameTextInput = (TextInputEditText) findViewById(R.id.dataExameTextInput);
        descricaoExameTextInput = (TextInputEditText) findViewById(R.id.descricaoExameTextInput);

        dataExameTextInput.addTextChangedListener(MaskEditUtil.mask(dataExameTextInput, MaskEditUtil.FORMAT_DATE));

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //selecionar um pdf
        addfloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(ExameAddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    selectPDF();
                }
                else{
                    ActivityCompat.requestPermissions(ExameAddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 86);
                }
            }
        });
        //fazer o upload no Storage e no Realtime database
        uploadExameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // data, no presenter
                if (uriPDF != null ) {
                    if(descricaoExameTextInput.getText().toString() != ""){
                        uploadFile(uriPDF);
                    }else{
                        Toast.makeText(ExameAddActivity.this, "Você deve adicionar uma descrição", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ExameAddActivity.this, "Você deve selecionar um arquivo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getUserId() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    //metodo para o upload
    private void uploadFile(Uri uriPDF) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Salvando Arquivo...");
        progressDialog.setProgress(0);
        progressDialog.show();

        getUserId();

        final String fileName = System.currentTimeMillis()+"";
        StorageReference storageReference = firebaseStorage.getReference();// root path
        //colocando o caminho
        storageReference.child("users").child("pacientes").child(userId).child("exames").child(fileName).putFile(uriPDF)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String urlExame = taskSnapshot.getUploadSessionUri().toString();//uri do pdf file no Storage
                    //incluir no realtime Database
                    DatabaseReference databaseReference = firebaseDatabase.getReference(); // root
                    Exame exame = new Exame();
                    exame.setDataExame(dataExameTextInput.getText().toString());
                    exame.setIdExame(fileName);
                    exame.setDescricaoExame(descricaoExameTextInput.getText().toString());
                    exame.setUrlExame(urlExame);
                    exame.setExameTimestamp(tryParseDatetoTimeStamp(dataExameTextInput.getText().toString(), "00:00"));

                    databaseReference.child("users").child("pacientes").child(userId).child("exames").child(fileName).setValue(exame).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ExameAddActivity.this, "Arquivo do exame gravado com sucesso", Toast.LENGTH_SHORT).show();
                                //volta para a Lista de Exame
                                Intent intent = new Intent(ExameAddActivity.this, ExameActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ExameAddActivity.this.startActivity(intent);
                                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                            }else{
                                Toast.makeText(ExameAddActivity.this, "Falha ao salvar o arquivo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExameAddActivity.this, "Falha ao salvar o arquivo", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    //Se o usuario der permissão no else, executa o onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 86 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPDF();
        }else{
            Toast.makeText(ExameAddActivity.this, "Sem permissão para ler arquivos", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {
        //para o usuário selecionar pelo fileManager
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 89);
    }

    //Depois de selecionar, executa esse método
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //garantir que o usuario selecionou o pdf
        if(requestCode == 89 && resultCode == RESULT_OK && data!=null){
            uriPDF = data.getData();//uri do arquivo selecionado
            statusExameTextView.setText("Arquivo selecionado: " + data.getData().getLastPathSegment());
        }else{
            Toast.makeText(ExameAddActivity.this, "Selecione um arquivo...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(ExameAddActivity.this, ExameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ExameAddActivity.this.startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }
}
