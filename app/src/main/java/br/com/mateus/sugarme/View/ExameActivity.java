package br.com.mateus.sugarme.View;
//falta o delete, o download, o controller
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.mateus.sugarme.BuildConfig;
import br.com.mateus.sugarme.Controller.ExameController;
import br.com.mateus.sugarme.DAO.ExameDAO;
import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.Exame;
import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Factory.NavigationFactory.FinishNavigation;
import static java.io.File.createTempFile;

public class ExameActivity extends AppCompatActivity {

    private List<Exame> exameList = new ArrayList<>();
    private ExameActivity.ExameArrayAdapter exameArrayAdapter;
    private ListView exameListView;
    private FirebaseAuth firebaseAuth;
    private static String userId;
    private String exameId;
    private DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(R.string.app_name_Exame);     //Titulo para ser exibido na sua Action Bar em frente à seta



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExameActivity.this, ExameAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        exameListView= (ListView) findViewById(R.id.exameListView);


        exameArrayAdapter = new ExameActivity.ExameArrayAdapter(this, exameList);
        exameListView.setAdapter(exameArrayAdapter);

        configuraObserverShortClick();
    }

    private void configuraObserverShortClick() {
        exameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Exame exame = exameList.get(position);
                exameId = exame.getIdExame();
//                Toast.makeText(ExameActivity.this, exameId, Toast.LENGTH_SHORT).show();
                //verifyStoragePermissions(ExameActivity.this);
                //downloadFromStorage(exameId);

                //
                // browseTo(exameId);

                /*

                AlertDialog.Builder dbuilder = new AlertDialog.Builder(ExameActivity.this);
                dbuilder.setPositiveButton(getString(R.string.downloadItemExame), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*DiarioGlicemico diarioGlicemico = exameListView.get(position);
                        DiarioGlicemicoDAO diarioGlicemicoDAO = new DiarioGlicemicoDAO();
                        diarioGlicemicoDAO.excluir(diarioGlicemico.getDiarioId());
                    }
                }).setNegativeButton(getString(R.string.deleteItemExame), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                dbuilder.show();*/
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("exames").orderByChild("exameTimestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exameList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    Exame todos = json.getValue(Exame.class);
                    todos.setIdExame(json.getKey());
                    exameList.add(todos);
                }
                //inverte a lista
                Collections.reverse(exameList);
                exameArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    public static class ExameArrayAdapter extends ArrayAdapter<Exame> {
        public ExameArrayAdapter (Context context, List<Exame> forecast){
            super (context, -1, forecast);
        }

        private static class ViewHolder{
            TextView dataExameTextView;
            TextView descricaoExameTextView;
            ImageView deleteExameImageView;
            ImageView downloadExameImageView;
            ImageView shareExameImageView;
            String exameId;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Exame dgc = getItem (position);
            final ExameActivity.ExameArrayAdapter.ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new ExameActivity.ExameArrayAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_exame, parent, false);
                viewHolder.dataExameTextView = (TextView) convertView.findViewById(R.id.dataExameTextView);
                viewHolder.descricaoExameTextView = (TextView) convertView.findViewById(R.id.descricaoExameTextView);
                viewHolder.deleteExameImageView = (ImageView) convertView.findViewById(R.id.deleteExameImageView);
                viewHolder.downloadExameImageView = (ImageView) convertView.findViewById(R.id.downloadExameImageView);
                viewHolder.shareExameImageView = (ImageView) convertView.findViewById(R.id.shareExameImageView);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ExameActivity.ExameArrayAdapter.ViewHolder)convertView.getTag();
            }

            final Context context = getContext();
            viewHolder.dataExameTextView.setText(String.valueOf(dgc.getDataExame()));
            viewHolder.descricaoExameTextView.setText(dgc.getDescricaoExame());
            viewHolder.exameId = dgc.getIdExame();

            //Deletar
            viewHolder.deleteExameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dbuilder = new AlertDialog.Builder(context);
                    dbuilder.setTitle("Deseja deletar esse exame?");
                    dbuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Delete do database e do storage
                            ExameDAO exameDAO = new ExameDAO();
                            exameDAO.excluir(viewHolder.exameId);                                                    }
                    }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dbuilder.show();
                }
            });

            //Compartilhar
            viewHolder.shareExameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dbuilder = new AlertDialog.Builder(context);
                    dbuilder.setTitle("Deseja compartilhar esse exame?");
                    dbuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ExameController exameController = new ExameController();
                            exameController.shareFileFromExternalStorage(viewHolder.exameId, userId, context);
                        }
                    }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dbuilder.show();
                }
            });

            //Download
            viewHolder.downloadExameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dbuilder = new AlertDialog.Builder(context);
                    dbuilder.setTitle("Deseja fazer o download desse exame?");
                    dbuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ExameController exameController = new ExameController();
                            exameController.downloadFromStorage(viewHolder.exameId, userId, context);
                        }
                    }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    dbuilder.show();
                }
            });


            return convertView;
        }

    }

    private void shareFile (String fileName){

    }

    /***
     * Métodos para fazer download do Storage
     */

    //Primeiro verifica se tem permissão para salvar na memoria interna

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Explain
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }


        } else {
            //downloadFromStorage(exameId);
        }
    }

    //Se der autorização
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
                    //downloadFromStorage(exameId);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(ExameActivity.this, "No podemos escribir sin tener permiso", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //Gravar no disco a pasta
    private static File getDirFromSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File sdcard = Environment.getExternalStorageDirectory()
                    .getAbsoluteFile();
            File dir = new File(sdcard, "SugarMe" + File.separator + "Exames");
            if (!dir.exists())
                dir.mkdirs();
            return dir;
        } else {
            return null;
        }
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                FinishNavigation(ExameActivity.this, PacienteActivity.class);
                break;
            default:break;
        }
        return true;
    }

}
