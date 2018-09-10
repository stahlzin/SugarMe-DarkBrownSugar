package br.com.mateus.sugarme.Model.Model;

import java.io.Serializable;

public class MedicalInfo implements Serializable{
    private String mediaGlicemica;
    private String peso;
    private String altura;
    private String tipoDiabetes;

    public MedicalInfo() {
    }

    public MedicalInfo(String mediaGlicemica, String peso, String altura, String tipoDiabetes) {
        this.mediaGlicemica = mediaGlicemica;
        this.peso = peso;
        this.altura = altura;
        this.tipoDiabetes = tipoDiabetes;
    }

    public String getMediaGlicemica() {
        return mediaGlicemica;
    }

    public void setMediaGlicemica(String mediaGlicemica) {
        this.mediaGlicemica = mediaGlicemica;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getTipoDiabetes() {
        return tipoDiabetes;
    }

    public void setTipoDiabetes(String tipoDiabetes) {
        this.tipoDiabetes = tipoDiabetes;
    }
}
