package br.com.mateus.sugarme.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ExameController {
    private DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;

    public ExameController(){

    }

    public void shareFileFromExternalStorage (String fileName, String userId, final Context activity){
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();


        storageRef.child("users").child("pacientes").child(userId).child("exames").child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                shareWith(uri.toString(), activity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }
    public void shareWith(String url, Context activity){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, "Exame: SugarMe");
        share.putExtra(Intent.EXTRA_TEXT, "Utilize o link abaixo para visualizar o arquivo: \n" + url);

        activity.startActivity(Intent.createChooser(share, "Compartilhar link!"));
    }


    public void downloadFromStorage (String fileName, String userId, final Context activity){

        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();


        storageRef.child("users").child("pacientes").child(userId).child("exames").child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                browseTo(uri.toString(), activity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    public void browseTo(String url, Context activity){

        if (!url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://" + url;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

}
