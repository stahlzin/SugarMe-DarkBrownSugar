package br.com.mateus.sugarme.View;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.Singleton.UserSingleton;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.toByteArray;
import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static br.com.mateus.sugarme.Factory.NavigationFactory.SimpleNavigation;

public class FotoPerfilActivity extends AppCompatActivity {

    private CircleImageView fotoPefilEditImageView;
    private ImageView voltarImageView;
    private ImageView cameraImageView;
    private ImageView salvarImageView;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    private Bitmap foto;


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
        voltarImageView = (ImageView) findViewById(R.id.voltarImageView);
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
                    SimpleNavigation(FotoPerfilActivity.this, PerfilActivity.class);
                }

            }
        });

        final UserSingleton globalVariable = (UserSingleton) getApplicationContext();
        if(globalVariable.getFotoPerfil() != null){
            this.fotoPefilEditImageView.setImageBitmap(globalVariable.getFotoPerfil());
        }else{
            this.fotoPefilEditImageView.setImageResource(R.drawable.perfil);
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



    public void getUserId() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }

    private void uploadImage (final Bitmap image){
        //gera um nomoe aleatório
        final String chave = "fotoPerfil";
        getUserId();
        byte [] data = toByteArray (image);
        //armazena no storage com extensão
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("users").child("pacientes").child(userId).child("fotoPerfil").child(chave+ PNG_EXTENSION).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        databaseReference.child("users").child("pacientes").child(userId).child("dados").child("fotoPerfil").setValue(downloadURL.toString());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FinishNavigation(FotoPerfilActivity.this, PerfilActivity.class);
                break;
            default:break;
        }
        return true;
    }



}
