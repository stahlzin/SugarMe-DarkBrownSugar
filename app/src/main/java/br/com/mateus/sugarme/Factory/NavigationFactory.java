package br.com.mateus.sugarme.Factory;

import android.app.Activity;
import android.content.Intent;

public class NavigationFactory  {

    public static void SimpleNavigation (Activity atual, Class proxima) {
        Intent intent = new Intent(atual, proxima);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        atual.startActivity(intent);

    }
}
