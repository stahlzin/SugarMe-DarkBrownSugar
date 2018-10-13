package br.com.mateus.sugarme.Model;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiarioGlicemicoDAO {
    DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DiarioGlicemico diarioGlicemico;
    private DatabaseReference databaseReference;
    private List<DiarioGlicemico> diarioGlicemicoList;
    private String diarioId;

    public DiarioGlicemicoDAO (){}

    //Inserir ou Atualizar
    public void inserir(DiarioGlicemico diarioGlicemico) {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("diario").push().setValue(diarioGlicemico);
    }

    //Pegar Id Usuario
    public void getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }

    public List<DiarioGlicemico> consultaPaciente() {
        diarioGlicemicoList = new ArrayList<DiarioGlicemico>();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("diario").orderByChild("gliTimestamp").limitToLast(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                diarioGlicemicoList.clear();
                for (DataSnapshot json : dataSnapshot.getChildren()) {
                    DiarioGlicemico todos = json.getValue(DiarioGlicemico.class);
                    diarioGlicemicoList.add(todos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return diarioGlicemicoList;
    }

    //Excluir
    public void excluir(String diarioId) {
        getUserId();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("diario").child(diarioId).removeValue();
    }



}
