package br.com.mateus.sugarme.Model;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import br.com.mateus.sugarme.View.MedicalInfoActivity;

public class MedicalInfoDAO {
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private MedicalInfo medicalInfo;


    //Inserir ou Atualizar
    public void inserir(MedicalInfo medicalInfo){
        getUserId();
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("pacientes").child(userId).child("InfoMedicas").setValue(medicalInfo);
    }

    //Pegar Id Usuario
    public void getUserId(){
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }


    //Consultar medico e ir para a tela de edição
    public void consultaInfoMedica(final Activity activity) {
        medicalInfo = new MedicalInfo();
        getUserId();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child("pacientes").child(userId).child("InfoMedicas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    medicalInfo = dataSnapshot.getValue(MedicalInfo.class);
                    //Trocar de Activity
                    Intent intent = new Intent(activity, MedicalInfoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("edit","editarInfo");
                    intent.putExtra("info", (Serializable) medicalInfo);
                    activity.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(activity, MedicalInfoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
