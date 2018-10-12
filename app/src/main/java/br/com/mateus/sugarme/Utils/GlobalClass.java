package br.com.mateus.sugarme.Utils;

import android.app.Application;

public class GlobalClass extends Application{

    private String nomeUser;
    private String dataNascUser;

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public String getDataNascUser() {
        return dataNascUser;
    }

    public void setDataNascUser(String dataNascUser) {
        this.dataNascUser = dataNascUser;
    }
}
