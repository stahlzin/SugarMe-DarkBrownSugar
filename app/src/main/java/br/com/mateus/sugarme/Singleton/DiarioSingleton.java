package br.com.mateus.sugarme.Singleton;

public class DiarioSingleton {
    private static DiarioSingleton instancia;

    public static DiarioSingleton getInstancia() {
        if (instancia == null) {
            instancia = new DiarioSingleton();
        }
        return instancia;
    }

    public DiarioSingleton(){

    }

    public int hiperglicemiaPad = 200;
    public int hipoglicemiaPad = 70;

}
