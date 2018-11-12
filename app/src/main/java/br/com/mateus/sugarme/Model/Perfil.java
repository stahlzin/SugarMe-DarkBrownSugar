package br.com.mateus.sugarme.Model;

import java.io.Serializable;

public class Perfil implements Serializable{
    private String peso;
    private String altura;

    private String tipoDiabetes;
    private String anoDescoberta;
    private String anoInicioTratamento;

    private int medicacao;
    private int alimentar;
    private int insulina;
    private int esporte;

    private int colesterol;
    private int pressaoAlta;
    private int obesidade;
    private int triglicerídeos;
    private int sedentarismo;


    public Perfil() {
    }

    public Perfil(String peso, String altura, String tipoDiabetes, String anoDescoberta, String anoInicioTratamento, int medicacao, int alimentar, int insulina, int esporte, int colesterol, int pressaoAlta, int obesidade, int triglicerídeos, int sedentarismo) {
        this.peso = peso;
        this.altura = altura;
        this.tipoDiabetes = tipoDiabetes;
        this.anoDescoberta = anoDescoberta;
        this.anoInicioTratamento = anoInicioTratamento;
        this.medicacao = medicacao;
        this.alimentar = alimentar;
        this.insulina = insulina;
        this.esporte = esporte;
        this.colesterol = colesterol;
        this.pressaoAlta = pressaoAlta;
        this.obesidade = obesidade;
        this.triglicerídeos = triglicerídeos;
        this.sedentarismo = sedentarismo;
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

    public String getAnoDescoberta() {
        return anoDescoberta;
    }

    public void setAnoDescoberta(String anoDescoberta) {
        this.anoDescoberta = anoDescoberta;
    }

    public String getAnoInicioTratamento() {
        return anoInicioTratamento;
    }

    public void setAnoInicioTratamento(String anoInicioTratamento) {
        this.anoInicioTratamento = anoInicioTratamento;
    }

    public int getMedicacao() {
        return medicacao;
    }

    public void setMedicacao(int medicacao) {
        this.medicacao = medicacao;
    }

    public int getAlimentar() {
        return alimentar;
    }

    public void setAlimentar(int alimentar) {
        this.alimentar = alimentar;
    }

    public int getInsulina() {
        return insulina;
    }

    public void setInsulina(int insulina) {
        this.insulina = insulina;
    }

    public int getEsporte() {
        return esporte;
    }

    public void setEsporte(int esporte) {
        this.esporte = esporte;
    }

    public int getColesterol() {
        return colesterol;
    }

    public void setColesterol(int colesterol) {
        this.colesterol = colesterol;
    }

    public int getPressaoAlta() {
        return pressaoAlta;
    }

    public void setPressaoAlta(int pressaoAlta) {
        this.pressaoAlta = pressaoAlta;
    }

    public int getObesidade() {
        return obesidade;
    }

    public void setObesidade(int obesidade) {
        this.obesidade = obesidade;
    }

    public int getTriglicerídeos() {
        return triglicerídeos;
    }

    public void setTriglicerídeos(int triglicerídeos) {
        this.triglicerídeos = triglicerídeos;
    }

    public int getSedentarismo() {
        return sedentarismo;
    }

    public void setSedentarismo(int sedentarismo) {
        this.sedentarismo = sedentarismo;
    }
}
