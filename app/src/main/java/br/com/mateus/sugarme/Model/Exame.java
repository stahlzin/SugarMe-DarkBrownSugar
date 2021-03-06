package br.com.mateus.sugarme.Model;

import com.google.firebase.database.Exclude;

public class Exame {

    private String idExame;
    private String dataExame;
    private String urlExame;
    private String descricaoExame;
    private String exameTimestamp;


    public Exame(){}

    public Exame(String idExame, String dataExame, String urlExame, String descricaoExame, String exameTimestamp) {
        this.idExame = idExame;
        this.dataExame = dataExame;
        this.urlExame = urlExame;
        this.descricaoExame = descricaoExame;
        this.exameTimestamp = exameTimestamp;
    }

    @Exclude
    public String getIdExame() {
        return idExame;
    }

    public void setIdExame(String idExame) {
        this.idExame = idExame;
    }

    public String getDataExame() {
        return dataExame;
    }

    public void setDataExame(String dataExame) {
        this.dataExame = dataExame;
    }

    public String getUrlExame() {
        return urlExame;
    }

    public void setUrlExame(String urlExame) {
        this.urlExame = urlExame;
    }

    public String getDescricaoExame() {
        return descricaoExame;
    }

    public void setDescricaoExame(String descricaoExame) {
        this.descricaoExame = descricaoExame;
    }

    public String getExameTimestamp() {
        return exameTimestamp;
    }

    public void setExameTimestamp(String exameTimestamp) {
        this.exameTimestamp = exameTimestamp;
    }
}
