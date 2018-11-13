package br.com.mateus.sugarme.DAO;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.mateus.sugarme.Model.Intercorrencia;

import static br.com.mateus.sugarme.View.MainController.getUserId;

public class IntercorrenciaDAO {
    private DatabaseReference mDatabase;
    private String userId;

    public IntercorrenciaDAO(){}

    //Inserir
    public void inserir(Intercorrencia intercorrencia){
        userId = getUserId();
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("Intercorrencias").push().setValue(intercorrencia);
    }

    //Excluir
    public void excluir(String intercorrenciaId) {
        userId = getUserId();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("Intercorrencias").child(intercorrenciaId).removeValue();
    }

}
