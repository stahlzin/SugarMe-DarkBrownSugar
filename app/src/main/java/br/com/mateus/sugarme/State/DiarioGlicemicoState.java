package br.com.mateus.sugarme.State;

import android.app.Activity;

import br.com.mateus.sugarme.R;

public class DiarioGlicemicoState {

    public static int getStateBackgroundColor (int value, int hiperPad, int hipoPad){

        if (value <= hipoPad) {
            return R.color.colorHipo;
        }
        else if (value >= hiperPad) {
            return R.color.colorHiper;
        }
        else {
            return R.color.colorNormal;
        }
    }

}
