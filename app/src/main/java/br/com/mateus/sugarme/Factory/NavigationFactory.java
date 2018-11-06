package br.com.mateus.sugarme.Factory;

import android.app.Activity;
import android.content.Intent;

public class NavigationFactory  {

    /**Métodos para executar a navegação de forma simples, sem desempilhar a activity
     * Utilizado em botões para abrir nova activity
     * @param atual
     * @param proxima
     */

    public static void SimpleNavigation (Activity atual, Class proxima) {
        Intent intent = new Intent(atual, proxima);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        atual.startActivity(intent);
    }


    /**
     * Método para executar a navegação, mas desempinhando a activity atual.
     * Utilizado em botões voltar
     * @param atual
     * @param proxima
     */
    public static void FinishNavigation (Activity atual, Class proxima) {
        Intent intent = new Intent(atual, proxima);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        atual.startActivity(intent);
        atual.finishAffinity();
    }

    /**Métodos para executar a navegação de forma simples, sem desempilhar a activity
     * Utilizado em botões para abrir nova activity
     * @param atual
     * @param proxima
     */

    public static void NavigationWithOnePutExtra (Activity atual, Class proxima, String name, String value) {
        Intent intent = new Intent(atual, proxima);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(name, value);
        atual.startActivity(intent);
    }
}
