package br.com.mateus.sugarme.Model;

public class Intercorrencia {
    private String dataIntercorrencia;
    private String anotacoes;
    private int hiperglicemia;
    private int hipoglicemia;
    private int sedeExcessiva;
    private int nausea;

    public Intercorrencia(String dataIntercorrencia, String anotacoes,
                          int hiperglicemia, int hipoglicemia, int sedeExcessiva, int nausea) {
        this.dataIntercorrencia = dataIntercorrencia;
        this.anotacoes = anotacoes;
        this.hiperglicemia = hiperglicemia;
        this.hipoglicemia = hipoglicemia;
        this.sedeExcessiva = sedeExcessiva;
        this.nausea = nausea;
    }

    public Intercorrencia() {

    }

    public String getDataIntercorrencia() {

        return dataIntercorrencia;
    }

    public void setDataIntercorrencia(String dataIntercorrencia) {
        this.dataIntercorrencia = dataIntercorrencia;
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
}
