package br.com.mateus.sugarme.Presenter;

import android.app.Activity;
import android.widget.Toast;

import br.com.mateus.sugarme.Model.Intercorrencia;

public class IntercorrenciaPresenter {

    public boolean isDadosOk(Intercorrencia intercorrencia, Activity activity) {
            if(!intercorrencia.getDataIntercorrencia().isEmpty()){
                if(intercorrencia.getHiperglicemia() ==1 ||
                        intercorrencia.getHipoglicemia() ==1 ||
                        intercorrencia.getSedeExcessiva() == 1 ||
                        intercorrencia.getNausea() == 1 ||
                        intercorrencia.getDesmaio() == 1 ||
                        intercorrencia.getInternacao() == 1){
                    if(!intercorrencia.getAnotacoes().isEmpty()){
                        if(intercorrencia.getHiperglicemia() != intercorrencia.getHipoglicemia()){
                            return true;
                        }else{
                            Toast.makeText(activity, "Hipoglicemia e Hiperglicemia não podem ser registradas juntas!", Toast.LENGTH_SHORT).show();
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
