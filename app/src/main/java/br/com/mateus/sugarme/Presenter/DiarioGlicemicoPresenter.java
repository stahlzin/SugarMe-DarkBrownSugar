package br.com.mateus.sugarme.Presenter;

import android.app.Activity;
import android.widget.Toast;

import br.com.mateus.sugarme.Model.DiarioGlicemico;
import br.com.mateus.sugarme.Model.DiarioGlicemicoDAO;

public class DiarioGlicemicoPresenter {
    private DiarioGlicemicoDAO diarioGlicemicoDAO = new DiarioGlicemicoDAO();

    public DiarioGlicemicoPresenter (){}

    public boolean isDadosOk(DiarioGlicemico diarioGlicemico, Activity activity){

        if(diarioGlicemico.getGlicemia() != -1){
            if(!diarioGlicemico.getData().isEmpty()){
                if(!diarioGlicemico.getHora().isEmpty()){
                    return true;
                }
                else {
                    Toast.makeText(activity, "Hora inválido!", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(activity, "Data inválida!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(activity, "Índice glicemico inválido!", Toast.LENGTH_SHORT).show();
        }

        return false;
    }






}
