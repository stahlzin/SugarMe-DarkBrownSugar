package br.com.mateus.sugarme.Controller;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConfigurarController {
    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;
    private static String userId;

    public static void alterarCofigurarPaciente (String userId, String aceitaChat, String compartilharDiario, String hipoglicemia, String hiperglicemia){

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("aceitaChat").setValue(aceitaChat);
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("compartilharDiario").setValue(compartilharDiario);
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("hipoglicemia").setValue(hipoglicemia);
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("hiperglicemia").setValue(hiperglicemia);

    }

    public static void alterarCofigurarMedico (String userId, String aceitaChat){

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("medicos").child(userId).child("configurar").child("aceitaChat").setValue(aceitaChat);
    }

    public static List<String> lerConfigurarPaciente (String userId){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final List<String> dadosPaciente = new ArrayList<>();
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*dadosPaciente.set(0, dataSnapshot.child("aceitaChat").getChildren().toString());
                dadosPaciente.set(1,dataSnapshot.child("compartilharDiario").getChildren().toString());
                dadosPaciente.set(2, dataSnapshot.child("hipoglicemia").getChildren().toString());
                dadosPaciente.set(3, dataSnapshot.child("hiperglicemia").getChildren().toString());*/
                dadosPaciente.set(0, "sim");
                dadosPaciente.set(1,"sim");
                dadosPaciente.set(2, "30");
                dadosPaciente.set(3, "90");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return dadosPaciente;
    }


    public static String getUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        return userId = firebaseAuth.getCurrentUser().getUid();
    }

}
