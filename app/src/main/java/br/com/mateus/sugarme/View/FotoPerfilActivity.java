package br.com.mateus.sugarme.View;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Singleton.UserSingleton;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.toBitmap;
import static br.com.mateus.sugarme.Builder.CoverterBuilder.toByteArray;
import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.SimpleNavigation;
import static br.com.mateus.sugarme.View.MainController.getUserId;

public class FotoPerfilActivity extends AppCompatActivity {

    private CircleImageView fotoPefilEditImageView;

    private ImageView cameraImageView;
    private ImageView salvarImageView;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    private Bitmap foto;
    private String userType;



    //camera things
    private static final int REQUEST_PERMISSION_CAMERA = 2001;
    private static final int REQUEST_TAKE_PICTURE = 1001;
    private static final String PNG_EXTENSION = ".png";
    private String userId;


    //referência ao diretório images
    private StorageReference imagesReference;
    //referência para gerar nomes de arquivos únicos
    private DatabaseReference fileNameGenerator;
    //referência para guardar as urls das fotos cujo upload foi realizado
    private DatabaseReference urlsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_FotoPerfil);     //Titulo para ser exibido na sua Action Bar em frente à seta

        fotoPefilEditImageView = (CircleImageView) findViewById(R.id.fotoPefilEditImageView);

        cameraImageView = (ImageView) findViewById(R.id.cameraImageView);
        salvarImageView = (ImageView) findViewById(R.id.salvarImageView);


        cameraImageView.setOnClickListener(fabListener);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        salvarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foto == null){

                }else{
                    final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
                    globalVariable.setFotoPerfil(foto);
                    uploadImage(foto);
                    if(userType.equals("pacientes")){
                        FinishNavigation(FotoPerfilActivity.this, PerfilActivity.class);
                    }else if(userType.equals("medicos")){
                        FinishNavigation(FotoPerfilActivity.this, MedicoActivity.class);
                    }
                }

            }
        });

        final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
        if(globalVariable.getFotoPerfil() != null){
            this.fotoPefilEditImageView.setImageBitmap(globalVariable.getFotoPerfil());
        }else{
            this.fotoPefilEditImageView.setImageResource(R.drawable.perfil);
        }

        Intent it = getIntent();
        if(it != null && it.getExtras() != null) {
            if (it.getStringExtra("tipo").equals("medico")) {
                userType = "medicos";
                getMedicalFoto();
            }
            if (it.getStringExtra("tipo").equals("paciente")) {
                userType = "pacientes";
            }
        }
    }

   private View.OnClickListener fabListener = new View.OnClickListener() {
        public void onClick(View v) {
            //verifica se já tem permissão
            if (ActivityCompat.checkSelfPermission(FotoPerfilActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                //verifica se deve explicar
                if (ActivityCompat.shouldShowRequestPermissionRationale(FotoPerfilActivity.this,
                        Manifest.permission.CAMERA)){
                    Toast.makeText(FotoPerfilActivity.this,"Deu ruim", Toast.LENGTH_SHORT).show();
                }
                //se não tem permissão, pede
                ActivityCompat.requestPermissions(FotoPerfilActivity.this, new String[]
                        {Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }
            else{
                //se já tem, tira foto
                goTakePicture();
            }
        }
    };

    private void goTakePicture (){
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicIntent, REQUEST_TAKE_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CAMERA:
                //usuário disse sim
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    goTakePicture();
                }
                //usuário disse não
                else{
                    Toast.makeText(this, "Ruim",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_TAKE_PICTURE:
                //usuário tirou foto
                if (resultCode == Activity.RESULT_OK){
                    //pega a foto
                    foto = (Bitmap) data.getExtras().get("data");
                    //faz upload para o Firebase Storage
                    updateImage(foto);
                }
                else{
                    Toast.makeText(this,"Não foi", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateImage(Bitmap bitmap){
        fotoPefilEditImageView.setImageBitmap(bitmap);
    }




    private void uploadImage (final Bitmap image){
        //gera um nomoe aleatório
        final String chave = "fotoPerfil";
        userId = getUserId();
        byte [] data = toByteArray (image);
        //armazena no storage com extensão
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("users").child(userType).child(userId).child("fotoPerfil").child(chave+ PNG_EXTENSION).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //updateImage(image);
                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                //no database a chave tem que ser sem extensão, por causa do ponto
                saveURLForDownload (downloadUrl);
                Toast.makeText(FotoPerfilActivity.this, "OK",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FotoPerfilActivity.this, "ruim",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }



    private void saveURLForDownload (Uri downloadURL){
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users").child(userType).child(userId).child("dados").child("fotoPerfil").setValue(downloadURL.toString());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(userType.equals("pacientes")){
                    FinishNavigation(FotoPerfilActivity.this, PerfilActivity.class);
                }else if(userType.equals("medicos")){
                    FinishNavigation(FotoPerfilActivity.this, MedicoActivity.class);
                }
                break;
            default:break;
        }
        return true;
    }

    private void getMedicalFoto(){
        final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
        if(globalVariable.getFotoPerfil() == null){
            downloadImageProfile();
        }else{
            fotoPefilEditImageView.setImageBitmap(globalVariable.getFotoPerfil());
        }
    }

    private void downloadImageProfile() {
        userId = getUserId();
        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();
        //Download file in Memory
        StorageReference islandRef = storageRef.child("users").child("medicos").child(userId).child("fotoPerfil/fotoPerfil.png");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
                globalVariable.setFotoPerfil(toBitmap(bytes));
                fotoPefilEditImageView.setImageBitmap(globalVariable.getFotoPerfil());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

}
