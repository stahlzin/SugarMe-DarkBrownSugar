package br.com.mateus.sugarme.Controller;

import android.app.Activity;
import android.widget.Toast;

import br.com.mateus.sugarme.Model.Perfil;
import br.com.mateus.sugarme.DAO.PerfilDAO;


public class PerfilController {
    private PerfilDAO perfilDAO = new PerfilDAO();

    public boolean isDadosOk(Perfil perfil, Activity activity){

            if(!perfil.getPeso().isEmpty()){
                if(!perfil.getAltura().isEmpty()){
                    if(!perfil.getTipoDiabetes().isEmpty()){
                        return true;
                    }
                    else {
                        Toast.makeText(activity, "Tipo inválido!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(activity, "Altura inválida!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(activity, "Peso inválido!", Toast.LENGTH_SHORT).show();
            }

        return false;
    }


    public void recebeInfoMedica(Activity activity) {
        perfilDAO.consultaInfoMedica(activity);
    }

    public void getPerfil (Activity activity){
        perfilDAO.buscaPerfil(activity);
    }
}


