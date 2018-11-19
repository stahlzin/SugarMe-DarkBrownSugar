package br.com.mateus.sugarme.Builder;

import java.util.ArrayList;

public class DataBuilder {
    public static ArrayList getUfList(){

        ArrayList<String> uf = new ArrayList<>();

        uf.add("AC");
        uf.add("AL");
        uf.add("AM");
        uf.add("AP");
        uf.add("BA");
        uf.add("CE");
        uf.add("DF");
        uf.add("ES");
        uf.add("GO");
        uf.add("MA");
        uf.add("MG");
        uf.add("MS");
        uf.add("MT");
        uf.add("PA");
        uf.add("PB");
        uf.add("PE");
        uf.add("PI");
        uf.add("PR");
        uf.add("RJ");
        uf.add("RN");
        uf.add("RO");
        uf.add("RR");
        uf.add("RS");
        uf.add("SC");
        uf.add("SE");
        uf.add("SP");
        uf.add("TO");

        return uf ;
    }

    public static String getTypeOfDiabetes(String numberDiabete){
        String infoTipo;
        switch (numberDiabete){
            case ("1"): infoTipo = "1"; break;
            case ("2"): infoTipo = "2";break;
            case ("3"): infoTipo = "Gestacional"; break;
            default: infoTipo = ""; break;
        }
        return infoTipo;
    }

    public static String getTreamentProfile (int medicacao, int insulina, int alimentar, int esporte){

        if(medicacao == 1 && insulina == 1 && alimentar == 1 && esporte == 1){
            return "Medicação, Insulina, Dieta Restritiva e Prática de Atividades Físicas";
        }
        if(medicacao == 1 && insulina == 1 && alimentar == 1 && esporte == 0){
            return "Medicação, Insulina e Dieta Restritiva";
        }
        if(medicacao == 1 && insulina == 1 && alimentar == 0 && esporte == 0){
            return "Medicação e Insulina";
        }
        if(medicacao == 1 && insulina == 0 && alimentar == 0 && esporte == 0){
            return "Medicação";
        }
        if(medicacao == 0 && insulina == 1 && alimentar == 1 && esporte == 1){
            return "Insulina, Dieta Restritiva e Prática de Atividades Físicas";
        }
        if(medicacao == 0 && insulina == 1 && alimentar == 1 && esporte == 0){
            return "Insulina e Dieta Restritiva";
        }
        if(medicacao == 0 && insulina == 1 && alimentar == 0 && esporte == 0){
            return "Insulina";
        }
        if(medicacao == 0 && insulina == 0 && alimentar == 1 && esporte == 1){
            return "Dieta Restritiva e Prática de Atividades Físicas";
        }
        if(medicacao == 0 && insulina == 0 && alimentar == 1 && esporte == 0){
            return "Dieta Restritiva";
        }
        if(medicacao == 0 && insulina == 0 && alimentar == 0 && esporte == 1){
            return "Prática de Atividades Físicas";
        }
        if(medicacao == 1 && insulina == 0 && alimentar == 1 && esporte == 1){
            return "Medicação, Dieta Restritiva e Prática de Atividades Físicas";
        }
        if(medicacao == 0 && insulina == 0 && alimentar == 0 && esporte == 0){
            return "Sem informações do tratamento";
        }
        return "";
    }

    public static String getSintomasInter (){
        return "done";
    }

    public static ArrayList getDescExamList(){

        ArrayList<String> desc = new ArrayList<>();

        desc.add("Rotina Sanguínea");
        desc.add("Rotina Urina");
        desc.add("Rotina Combinada: Sangue e Urina");
        desc.add("Curva Glicêmica");
        desc.add("Glicemia de jejum");
        desc.add("Teste de tolerância à glicose (TOTG)");
        desc.add("Glicemia pós-prandial");
        desc.add("Glicosúria");
        desc.add("Hemoglobina glicada (HbA1C)");
        desc.add("T4/TSH");
        desc.add("Triglicérides");
        desc.add("Colesterol total");
        desc.add("HDL colesterol");
        desc.add("LDL colesterol");
        desc.add("Creatinina");
        desc.add("Urina I");
        desc.add("Relação Albumina/creatinina (A/C)");
        desc.add("Pesquisa de microalbuminúria ");
        desc.add("Proteinúria de 24 horas");
        desc.add("Taxa de filtração glomerular");
        desc.add("Pesquisa de co-morbidades");
        desc.add("Dosagem do peptídeo C");
        desc.add("Dosagem de auto-anticorpos anti GAD e anti-insulina");
        desc.add("RX de punho e mãos para avaliação da idade óssea");
        desc.add("Pesquisa de Doença celíaca (transglutaminase)");
        desc.add("Outros exames");

        return desc ;
    }

    public static ArrayList getMesList(){

        ArrayList<String> mes = new ArrayList<>();

        mes.add("Janeiro");
        mes.add("Fevereiro");
        mes.add("Março");
        mes.add("Abril");
        mes.add("Maio");
        mes.add("Junho");
        mes.add("Julho");
        mes.add("Agosto");
        mes.add("Setembro");
        mes.add("Outubro");
        mes.add("Novembro");
        mes.add("Dezembro");


        return mes ;
    }

}
