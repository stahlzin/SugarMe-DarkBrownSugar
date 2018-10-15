package br.com.mateus.sugarme.Utils;

import android.app.Application;

public class GlobalClass extends Application{

    //para o perfil
    private String nomeUser;
    private String dataNascUser;
    private String tipoDiabetes;
    private String tratamento;
    private String inicioTratamento;

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

    public String getTipoDiabetes() {
        return tipoDiabetes;
    }

    public void setTipoDiabetes(String tipoDiabetes) {
        this.tipoDiabetes = tipoDiabetes;
    }

    public String getTratamento() {
        return tratamento;
    }

    public void setTratamento(String tratamento) {
        this.tratamento = tratamento;
    }

    public String getInicioTratamento() {
        return inicioTratamento;
    }

    public void setInicioTratamento(String inicioTratamento) {
        this.inicioTratamento = inicioTratamento;
    }
}
