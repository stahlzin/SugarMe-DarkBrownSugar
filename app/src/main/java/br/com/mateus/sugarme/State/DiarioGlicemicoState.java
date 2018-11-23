package br.com.mateus.sugarme.State;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.mateus.sugarme.R;
import br.com.mateus.sugarme.View.DiarioGlicemicoActivity;

import static br.com.mateus.sugarme.Builder.CoverterBuilder.tryParseInt;
import static br.com.mateus.sugarme.View.MainController.getUserId;

public class DiarioGlicemicoState {

    public static DatabaseReference databaseReference;
    public static String userId;


    public static String createDiarioCat (int comp, int hipoPad, int hiperPad){

        if (comp <= hipoPad){
            return  "Hipoglicemia";
        }

        if (comp >= hiperPad){
            return "Hiperglicemia";
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
