package br.com.mateus.sugarme.Model;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IntercorrenciaDAO {
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private Intercorrencia intercorrencia;
    private Paciente paciente;
    private List<Intercorrencia> intercorrenciaList;

    public IntercorrenciaDAO(){}

    //Inserir
    public void inserir(Intercorrencia intercorrencia){
        getUserId();
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("Intercorrencias").push().setValue(intercorrencia);
    }

    //Pegar Id Usuario
    public void getUserId(){
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }

    //Excluir
    public void excluir(String intercorrenciaId) {
        getUserId();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("Intercorrencias").child(intercorrenciaId).removeValue();
    }




}
