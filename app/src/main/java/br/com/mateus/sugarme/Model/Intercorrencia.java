package br.com.mateus.sugarme.Model;

import com.google.firebase.database.Exclude;

public class Intercorrencia {
    private String id;
    private String dataIntercorrencia;
    private String horaIntercorrencia;
    private String anotacoes;
    private int hiperglicemia;
    private int hipoglicemia;
    private int sedeExcessiva;
    private int nausea;
    private int desmaio;
    private int internacao;
    private int caimbra;
    private int cansaso;
    private int visão;
    private int miccao;
    private String interTimestamp;

    public Intercorrencia() {

    }

    public Intercorrencia(String id, String dataIntercorrencia, String horaIntercorrencia, String anotacoes, int hiperglicemia, int hipoglicemia, int sedeExcessiva, int nausea, int desmaio, int internacao, int caimbra, int cansaso, int visão, int miccao, String interTimestamp) {
        this.id = id;
        this.dataIntercorrencia = dataIntercorrencia;
        this.horaIntercorrencia = horaIntercorrencia;
        this.anotacoes = anotacoes;
        this.hiperglicemia = hiperglicemia;
        this.hipoglicemia = hipoglicemia;
        this.sedeExcessiva = sedeExcessiva;
        this.nausea = nausea;
        this.desmaio = desmaio;
        this.internacao = internacao;
        this.caimbra = caimbra;
        this.cansaso = cansaso;
        this.visão = visão;
        this.miccao = miccao;
        this.interTimestamp = interTimestamp;
    }

    public int getCaimbra() {
        return caimbra;
    }

    public void setCaimbra(int caimbra) {
        this.caimbra = caimbra;
    }

    public int getCansaso() {
        return cansaso;
    }

    public void setCansaso(int cansaso) {
        this.cansaso = cansaso;
    }

    public int getVisão() {
        return visão;
    }

    public void setVisão(int visão) {
        this.visão = visão;
    }

    public int getMiccao() {
        return miccao;
    }

    public void setMiccao(int miccao) {
        this.miccao = miccao;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataIntercorrencia() {
        return dataIntercorrencia;
    }

    public void setDataIntercorrencia(String dataIntercorrencia) {
        this.dataIntercorrencia = dataIntercorrencia;
    }

    public String getHoraIntercorrencia() {
        return horaIntercorrencia;
    }

    public void setHoraIntercorrencia(String horaIntercorrencia) {
        this.horaIntercorrencia = horaIntercorrencia;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public int getHiperglicemia() {
        return hiperglicemia;
    }

    public void setHiperglicemia(int hiperglicemia) {
        this.hiperglicemia = hiperglicemia;
    }

    public int getHipoglicemia() {
        return hipoglicemia;
    }

    public void setHipoglicemia(int hipoglicemia) {
        this.hipoglicemia = hipoglicemia;
    }

    public int getSedeExcessiva() {
        return sedeExcessiva;
    }

    public void setSedeExcessiva(int sedeExcessiva) {
        this.sedeExcessiva = sedeExcessiva;
    }

    public int getNausea() {
        return nausea;
    }

    public void setNausea(int nausea) {
        this.nausea = nausea;
    }

    public int getDesmaio() {
        return desmaio;
    }

    public void setDesmaio(int desmaio) {
        this.desmaio = desmaio;
    }

    public int getInternacao() {
        return internacao;
    }

    public void setInternacao(int internacao) {
        this.internacao = internacao;
    }

    public String getInterTimestamp() {
        return interTimestamp;
    }

    public void setInterTimestamp(String interTimestamp) {
        this.interTimestamp = interTimestamp;
    }
}
