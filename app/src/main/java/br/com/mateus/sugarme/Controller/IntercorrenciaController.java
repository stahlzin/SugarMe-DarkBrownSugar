package br.com.mateus.sugarme.Controller;

import android.app.Activity;
import android.widget.Toast;

import br.com.mateus.sugarme.Model.Intercorrencia;

public class IntercorrenciaController {

    public boolean isDadosOk(Intercorrencia intercorrencia, Activity activity) {
            if(!intercorrencia.getDataIntercorrencia().isEmpty()){
                if(intercorrencia.getHiperglicemia() ==1 ||
                        intercorrencia.getHipoglicemia() ==1 ||
                        intercorrencia.getSedeExcessiva() == 1 ||
                        intercorrencia.getNausea() == 1 ||
                        intercorrencia.getDesmaio() == 1 ||
                        intercorrencia.getInternacao() == 1 ||
                        intercorrencia.getCaimbra() == 1 ||
                        intercorrencia.getCansaso() == 1 ||
                        intercorrencia.getVisão() == 1 ||
                        intercorrencia.getMiccao() == 1){
                    if(!intercorrencia.getAnotacoes().isEmpty()){
                        if(intercorrencia.getHiperglicemia() == 1 && intercorrencia.getHipoglicemia() == 1){
                            Toast.makeText(activity, "Hipoglicemia e Hiperglicemia não podem ser registradas juntas!", Toast.LENGTH_SHORT).show();
                        }else{
                            return true;
                        }
                    }
                    //Anotacao vazia
                    else{
                        Toast.makeText(activity, "Adicione uma anotação", Toast.LENGTH_SHORT).show();
                    }
                }
                //Nenhum sintoma selecionado
                else{
                    Toast.makeText(activity, "Selecione pelo menos 1 sintoma", Toast.LENGTH_SHORT).show();
                }

            }
            //Data vazia
            else{
                Toast.makeText(activity, "Preencha a data completa", Toast.LENGTH_SHORT).show();
            }
        return false;
    }
}
