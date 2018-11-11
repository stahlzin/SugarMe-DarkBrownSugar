package br.com.mateus.sugarme.View;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Singleton.UserSingleton;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.drawableToBitmap;
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
    private ImageView arquivoImageView;
    private int controleFotoAdd = 0;


    //camera things
    private static final int REQUEST_PERMISSION_CAMERA = 2001;
    private static final int REQUEST_TAKE_PICTURE = 1001;
    private static final String PNG_EXTENSION = ".png";
    private String userId;

    //ler pdf
    private static final int REQUEST_READ_PICTURE = 89;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 86;


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

        arquivoImageView = (ImageView) findViewById(R.id.arquivoImageView);
        cameraImageView = (ImageView) findViewById(R.id.cameraImageView);
        salvarImageView = (ImageView) findViewById(R.id.salvarImageView);


        cameraImageView.setOnClickListener(fabListener);

        arquivoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(FotoPerfilActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    selectImageFromStorage();
                }
                else{
                    ActivityCompat.requestPermissions(FotoPerfilActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_STORAGE);
                }

            }
        });

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        salvarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controleFotoAdd == 0){
                    Toast.makeText(FotoPerfilActivity.this,"Selecione uma imagem...", Toast.LENGTH_SHORT).show();
                }else{
                    if (foto == null){
                        uploadImage();
                        
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
                    //faz update do ImageView
                    updateImage(foto);
                    controleFotoAdd = 1;
                }
                else{
                    Toast.makeText(this,"Selecione uma imagem...", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_READ_PICTURE:
                if (resultCode == RESULT_OK && data!=null && data.getData() != null){
                    //pega a foto
                    mImageUri = data.getData();
                    Glide.with(this).load(mImageUri).into(fotoPefilEditImageView);
                    controleFotoAdd = 1;

                }else{
                    Toast.makeText(FotoPerfilActivity.this, "Selecione uma imagem...", Toast.LENGTH_SHORT).show();
                }
    }
    }

    private Uri mImageUri;
    private StorageTask mUploadTask;

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        if (mImageUri != null) {
            final String chave = "fotoPerfil";

            StorageReference fileReference = firebaseStorage.getReference();


            fileReference.child("users").child(userType).child(userId).child("fotoPerfil").child(chave+ getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(FotoPerfilActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FotoPerfilActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    /*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            //mProgressBar.setProgress((int) progress);
                        }
                    });*/
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImage(Bitmap bitmap){
        fotoPefilEditImageView.setImageBitmap(bitmap);
    }

    private void selectImageFromStorage() {
        //para o usuário selecionar pelo fileManager
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_READ_PICTURE);
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
