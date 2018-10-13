package br.com.mateus.sugarme.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class DiarioGlicemico implements Serializable {
    private String diarioId;
    private int glicemia;
    private String data;
    private String hora;
    private String categoria;
    private String gliTimestamp;


    public DiarioGlicemico() {
    }

    public DiarioGlicemico(String diarioId, int glicemia, String data, String hora, String categoria, String gliTimestamp) {
        this.diarioId = diarioId;
        this.glicemia = glicemia;
        this.data = data;
        this.hora = hora;
        this.categoria = categoria;
        this.gliTimestamp = gliTimestamp;
    }

    @Exclude
    public String getDiarioId() {
        return diarioId;
    }

    public void setDiarioId(String diarioId) {
        this.diarioId = diarioId;
    }

    public int getGlicemia() {
        return glicemia;
    }

    public void setGlicemia(int glicemia) {
        this.glicemia = glicemia;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getGliTimestamp() {
        return gliTimestamp;
    }

    public void setGliTimestamp(String gliTimestamp) {
        this.gliTimestamp = gliTimestamp;
    }
}