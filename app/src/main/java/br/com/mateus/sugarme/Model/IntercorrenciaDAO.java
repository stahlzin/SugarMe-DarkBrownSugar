package br.com.mateus.sugarme.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IntercorrenciaDAO {
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private Intercorrencia intercorrencia;
    private Paciente paciente;

    //Inserir
    public void inserir(Intercorrencia intercorrencia){
        getUserId();
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("Intercorrencias").child
                (intercorrencia.getDataIntercorrencia()).setValue(intercorrencia);
    }

    //Pegar Id Usuario
    public void getUserId(){
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }
}
