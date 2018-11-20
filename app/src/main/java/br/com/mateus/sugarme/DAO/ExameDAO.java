package br.com.mateus.sugarme.DAO;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import br.com.mateus.sugarme.Model.Exame;

import static br.com.mateus.sugarme.View.MainController.getUserId;

public class ExameDAO {
    DatabaseReference mDatabase;
    private String userId;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;



    public ExameDAO(){}

    public void inserir(Exame exame) {
        userId = getUserId();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("exames").child(exame.getIdExame()).setValue(exame);
    }

    //Excluir
    public void excluir(String exameId) {
        userId = getUserId();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("exames").child(exameId).removeValue();

        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("users").child("pacientes").child(userId).child("exames").child(exameId).delete();

    }




}
