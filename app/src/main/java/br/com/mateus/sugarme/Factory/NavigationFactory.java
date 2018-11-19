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

    public static void NavigationToCreateReport (Activity atual, Class proxima, String tipo, String tValue, String mes, String mValue, String ano, String aValue, String id, String idValue) {
        Intent intent = new Intent(atual, proxima);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(tipo, tValue);
        intent.putExtra(mes, mValue);
        intent.putExtra(ano, aValue);
        intent.putExtra(id, idValue);
        atual.startActivity(intent);
    }
}
