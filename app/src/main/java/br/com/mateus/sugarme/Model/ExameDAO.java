package br.com.mateus.sugarme.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExameDAO {
    DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;


    public ExameDAO(){}

    public void inserir(Exame exame) {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("exames").child(exame.getIdExame()).setValue(exame);
    }


}
