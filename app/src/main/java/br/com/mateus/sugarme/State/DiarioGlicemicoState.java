package br.com.mateus.sugarme.State;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.mateus.sugarme.R;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseInt;
import static br.com.mateus.sugarme.View.MainController.getUserId;

public class DiarioGlicemicoState {

    public static DatabaseReference databaseReference;
    public static String userId;
    public static int hipoPad;
    public static int hiperPad;

        public static String createDiarioCat (int comp){
            userId = getUserId();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.child("users").child("pacientes").child(userId).child("configurar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        hiperPad = tryParseInt(dataSnapshot.child("hiperglicemiaPadrao").getValue());
                        hipoPad = tryParseInt(dataSnapshot.child("hipoglicemiaPadrao").getValue());

                    }else{
                        hiperPad = 200;
                        hipoPad = 70;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (comp <= hipoPad){
                return "Hipoglicemia";
            }
            if (comp >= hiperPad){
                return  "Hiperglicemia";
            }

            return "Normal";
        }

        public static int getStateBackgroundColor (String value){

            if (value.equals("Hipoglicemia")) {
                return R.color.colorHipo;
            }
            if (value.equals("Hiperglicemia")) {
                return R.color.colorHiper;
            }

            return R.color.colorNormal;
        }
}
