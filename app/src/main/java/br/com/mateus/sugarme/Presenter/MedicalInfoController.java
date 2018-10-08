package br.com.mateus.sugarme.Presenter;

import android.app.Activity;
import android.widget.Toast;

import br.com.mateus.sugarme.Model.MedicalInfo;
import br.com.mateus.sugarme.Model.MedicalInfoDAO;


public class MedicalInfoController {
    private MedicalInfoDAO medicalInfoDAO = new MedicalInfoDAO();

    public boolean isDadosOk(MedicalInfo medicalInfo, Activity activity){
        if(!medicalInfo.getMediaGlicemica().isEmpty()){
            if(!medicalInfo.getPeso().isEmpty()){
                if(!medicalInfo.getAltura().isEmpty()){
                    if(!medicalInfo.getTipoDiabetes().isEmpty()){
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
        }
        else{
            Toast.makeText(activity, "Média glicêmica inválida!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    public void recebeInfoMedica(Activity activity) {
        medicalInfoDAO.consultaInfoMedica(activity);
    }
}