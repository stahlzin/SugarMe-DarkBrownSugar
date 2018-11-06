package br.com.mateus.sugarme.Singleton;

import android.app.Application;
import android.graphics.Bitmap;

public class UserSingleton extends Application{


    //para o paciente
    private String dataNascUser;
    private String tipoDiabetes;
    private String tratamento;
    private String inicioTratamento;
    private int hiperglicemiaPadrao = 200;
    private int hipoglicemiaPadrao = 70;
    private Bitmap fotoPerfil;

    //para o médico

    //para ambos
    private String nomeUser;
    private String aceitaChat;



    //Getters and Setters

    public String getAceitaChat() {
        return aceitaChat;
    }

    public void setAceitaChat(String aceitaChat) {
        this.aceitaChat = aceitaChat;
    }

    public Bitmap getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Bitmap fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public int getHiperglicemiaPadrao() {
        return hiperglicemiaPadrao;
    }

    public void setHiperglicemiaPadrao(int hiperglicemiaPadrao) {
        this.hiperglicemiaPadrao = hiperglicemiaPadrao;
    }

    public int getHipoglicemiaPadrao() {
        return hipoglicemiaPadrao;
    }

    public void setHipoglicemiaPadrao(int hipoglicemiaPadrao) {
        this.hipoglicemiaPadrao = hipoglicemiaPadrao;
    }

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
