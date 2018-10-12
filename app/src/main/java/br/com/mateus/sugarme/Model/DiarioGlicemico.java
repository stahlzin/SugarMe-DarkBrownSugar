package br.com.mateus.sugarme.Model;

import java.io.Serializable;

public class DiarioGlicemico implements Serializable {
    private int glicemia;
    private String data;
    private String hora;
    private String categoria;
    private String gliTimestamp;

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
